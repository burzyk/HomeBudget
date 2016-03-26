package com.jpbnetsoftware.homebudget.service

import java.time.LocalDate
import java.util.{Date, Base64}

import com.jpbnetsoftware.homebudget.data.OperationsRepository
import com.jpbnetsoftware.homebudget.domain.{CryptoHelper, StatementParser}
import com.jpbnetsoftware.homebudget.service.dto.{StatementGetResponse, OperationDetails, StatementUpdateRequest, StatementUpdateResponse}
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation._

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
  * Created by pburzynski on 21/03/2016.
  */
@RestController
class StatementController {
  @Autowired
  @BeanProperty
  var operationsRepository: OperationsRepository = _

  @Autowired
  @BeanProperty
  var statementParser: StatementParser = _

  @Autowired
  @BeanProperty
  var userProvider: UserProvider = _

  @Autowired
  @BeanProperty
  var cryptoHelper: CryptoHelper = _

  @RequestMapping(Array[String](UrlPaths.getStatementSequenceUrl))
  def getStatementSequence(@RequestParam index: Int, @RequestParam count: Int): StatementGetResponse = {

    val operations = operationsRepository.getOperations(
      userProvider.getCurrentUsername,
      userProvider.getCurrentPassword,
      index,
      count)

    new StatementGetResponse(operations
      .map(x => new OperationDetails(x._1, x._2.date, x._2.description, x._2.amount))
      .toList
      .asJava)
  }

  @RequestMapping(Array[String](UrlPaths.getStatementUrl))
  def getStatement(
                    @RequestParam @DateTimeFormat(pattern = "ddMMyyyy") from: LocalDate,
                    @RequestParam @DateTimeFormat(pattern = "ddMMyyyy") to: LocalDate): StatementGetResponse = {

    def normalizeDate(date: LocalDate) =
      if (date.isAfter(LocalDate.of(2500, 1, 1))) {
        LocalDate.of(2500, 1, 1)
      } else if (date.isBefore(LocalDate.of(1901, 1, 1))) {
        LocalDate.of(1901, 1, 1)
      } else {
        date
      }

    val effectiveFrom = normalizeDate(if (from == null) LocalDate.now().minusMonths(1) else from)
    val effectiveTo = normalizeDate(if (to == null) LocalDate.now().plusDays(1) else to)
    val operations = operationsRepository.getOperations(
      userProvider.getCurrentUsername,
      userProvider.getCurrentPassword,
      effectiveFrom,
      effectiveTo)

    new StatementGetResponse(operations
      .map(x => new OperationDetails(x._1, x._2.date, x._2.description, x._2.amount))
      .toList
      .asJava)
  }

  @RequestMapping(value = Array[String](UrlPaths.updateStatementUrl), method = Array[RequestMethod](RequestMethod.POST))
  def updateStatement(@RequestBody request: StatementUpdateRequest): StatementUpdateResponse = {
    val username = userProvider.getCurrentUsername
    val password = userProvider.getCurrentPassword

    val content = cryptoHelper.decodeBase64(request.base64QifOperations)
    val newOperations = statementParser.parse(content).toList

    if (newOperations.size == 0) {
      new StatementUpdateResponse(0, 0)
    }

    // operations are sorted descending
    val from = newOperations.last.date
    val to = newOperations.apply(0).date
    val toCompare = operationsRepository.getOperations(username, password, from, to).map(_._2).toList

    val toInsert = newOperations.filterNot(x => toCompare.contains(x))
    operationsRepository.insertOperations(username, password, toInsert)

    new StatementUpdateResponse(toInsert.size, newOperations.size - toInsert.size)
  }
}
