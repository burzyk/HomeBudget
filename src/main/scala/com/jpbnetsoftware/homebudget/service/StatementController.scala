package com.jpbnetsoftware.homebudget.service

import java.util.Base64

import com.jpbnetsoftware.homebudget.data.OperationsRepository
import com.jpbnetsoftware.homebudget.domain.StatementParser
import com.jpbnetsoftware.homebudget.service.dto.{StatementGetDto, OperationDetailsDto, StatementUpdateDto, StatementUpdateResponseDto}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestBody, RequestMapping, RequestMethod, RestController}

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
  var userIdProvider: UserIdProvider = _

  @RequestMapping(Array[String]("/statement"))
  def getStatement(): StatementGetDto = {
    val result = new StatementGetDto
    result.operations = operationsRepository.getOperations(userIdProvider.getCurrentUserId)
      .map(x => {
        val r = new OperationDetailsDto
        r.id = x.id
        r.description = x.description
        r.date = x.date
        r.amount = x.amount
        r
      })
      .asJava
    result
  }

  @RequestMapping(value = Array[String]("/statement"), method = Array[RequestMethod](RequestMethod.POST))
  def updateStatement(@RequestBody entity: StatementUpdateDto): StatementUpdateResponseDto = {
    val userId = userIdProvider.getCurrentUserId
    val content = String.valueOf(Base64.getDecoder.decode(entity.base64QifOperations).map(_.toChar))
    val operations = statementParser.parse(content)
    val toInsert = operations.filterNot(x => operationsRepository.operationExists(userId, x))

    toInsert.foreach(x => operationsRepository.insertOperation(userId, x))

    val result = new StatementUpdateResponseDto
    result.duplicatesCount = operations.size - toInsert.size
    result.insertedCount = toInsert.size

    result
  }
}
