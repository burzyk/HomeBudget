package com.jpbnetsoftware.homebudget.service

import java.time.LocalDate
import java.util.{Date, Base64}

import com.jpbnetsoftware.homebudget.data.OperationsRepository
import com.jpbnetsoftware.homebudget.domain.{CryptoHelper, StatementParser}
import com.jpbnetsoftware.homebudget.service.dto.{StatementGetDto, OperationDetailsDto, StatementUpdateDto, StatementUpdateResponseDto}
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

  @RequestMapping(Array[String](UrlPaths.getStatementUrl))
  def getStatement(
                    @RequestParam @DateTimeFormat(pattern = "ddMMyyyy") from: LocalDate,
                    @RequestParam @DateTimeFormat(pattern = "ddMMyyyy") to: LocalDate): StatementGetDto = {

    def normalizeDate(date: LocalDate) =
      if (date.isAfter(LocalDate.of(2500, 1, 1))) {
        LocalDate.of(2500, 1, 1)
      } else if (date.isBefore(LocalDate.of(1901, 1, 1))) {
        LocalDate.of(1901, 1, 1)
      } else {
        date
      }

    if (from == null || to == null) {
      new StatementGetDto(List[OperationDetailsDto]().asJava)
    }

    val effectiveFrom = normalizeDate(from)
    val effectiveTo = normalizeDate(to)
    val operations = operationsRepository.getOperations(
      userProvider.getCurrentUsername,
      userProvider.getCurrentPassword,
      effectiveFrom,
      effectiveTo)

    new StatementGetDto(operations
      .map(x => new OperationDetailsDto(x._1, x._2.date, x._2.description, x._2.amount))
      .toList
      .asJava)
  }

  @RequestMapping(value = Array[String](UrlPaths.updateStatementUrl), method = Array[RequestMethod](RequestMethod.POST))
  def updateStatement(@RequestBody entity: StatementUpdateDto): StatementUpdateResponseDto = {
    val username = userProvider.getCurrentUsername
    val password = userProvider.getCurrentPassword

    val content = cryptoHelper.decodeBase64(entity.base64QifOperations)
    val newOperations = statementParser.parse(content).toList

    if (newOperations.size == 0) {
      new StatementUpdateResponseDto(0, 0)
    }

    // operations are sorted descending
    val from = newOperations.last.date
    val to = newOperations.apply(0).date
    val toCompare = operationsRepository.getOperations(username, password, from, to).map(_._2).toList

    val toInsert = newOperations.filterNot(x => toCompare.contains(x))
    operationsRepository.insertOperations(username, password, toInsert)

    new StatementUpdateResponseDto(toInsert.size, newOperations.size - toInsert.size)
  }
}
