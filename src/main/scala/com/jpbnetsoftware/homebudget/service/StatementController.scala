package com.jpbnetsoftware.homebudget.service

import java.time.LocalDate

import com.jpbnetsoftware.homebudget.data.OperationsRepository
import com.jpbnetsoftware.homebudget.domain.{CryptoHelper, StatementParser}
import com.jpbnetsoftware.homebudget.service.dto._
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
      .map(x => new OperationDetails(x.id, x.date, x.description, x.amount))
      .asJava)
  }

  @RequestMapping(Array[String](UrlPaths.getBalancesUrl))
  def getBalances(
                   @RequestParam fromMonth: Int,
                   @RequestParam fromYear: Int,
                   @RequestParam toMonth: Int,
                   @RequestParam toYear: Int): StatementGetBalances = {

    val effectiveFrom = getEffectiveFrom(LocalDate.of(fromYear, fromMonth, 1))
    val effectiveTo = getEffectiveTo(LocalDate.of(toYear, toMonth, 1).plusMonths(1))
    val operations = operationsRepository.getOperations(
      userProvider.getCurrentUsername,
      userProvider.getCurrentPassword,
      effectiveFrom,
      effectiveTo)

    new StatementGetBalances(operations
      .groupBy(x => (x.date.getYear, x.date.getMonthValue))
      .map(x => new BalanceDetails(
        x._1._1,
        x._1._2,
        x._2.map(_.amount).filter(_ < 0).sum * -1,
        x._2.map(_.amount).filter(_ > 0).sum))
      .toList
      .sortBy(x => (x.year, + x.month))
      .reverse
      .asJava)
  }


  @RequestMapping(Array[String](UrlPaths.getStatementUrl))
  def getStatement(
                    @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") from: LocalDate,
                    @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") to: LocalDate): StatementGetResponse = {

    val effectiveFrom = getEffectiveFrom(from)
    val effectiveTo = getEffectiveTo(to)
    val operations = operationsRepository.getOperations(
      userProvider.getCurrentUsername,
      userProvider.getCurrentPassword,
      effectiveFrom,
      effectiveTo)

    new StatementGetResponse(operations
      .map(x => new OperationDetails(x.id, x.date, x.description, x.amount))
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
    val toCompare = operationsRepository.getOperations(username, password, from, to)

    val toInsert = newOperations.filterNot(x => toCompare.contains(x))
    operationsRepository.insertOperations(username, password, toInsert)

    new StatementUpdateResponse(toInsert.size, newOperations.size - toInsert.size)
  }

  private def getEffectiveTo(to: LocalDate) =
    normalizeDate(if (to == null) LocalDate.now().plusDays(1) else to)

  private def getEffectiveFrom(from: LocalDate) =
    normalizeDate(if (from == null) LocalDate.now().minusMonths(1) else from)

  private def normalizeDate(date: LocalDate) =
    if (date.isAfter(LocalDate.of(2500, 1, 1))) {
      LocalDate.of(2500, 1, 1)
    } else if (date.isBefore(LocalDate.of(1901, 1, 1))) {
      LocalDate.of(1901, 1, 1)
    } else {
      date
    }
}
