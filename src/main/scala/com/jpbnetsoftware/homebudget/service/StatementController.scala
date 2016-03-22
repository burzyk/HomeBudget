package com.jpbnetsoftware.homebudget.service

import java.util.Base64

import com.jpbnetsoftware.homebudget.data.OperationsRepository
import com.jpbnetsoftware.homebudget.domain.StatementParser
import com.jpbnetsoftware.homebudget.service.dto.{OperationsGetDto, OperationDetailsDto, OperationsUpdateDto, OperationsUpdateResponseDto}
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

  @RequestMapping(Array[String]("/statement/operations"))
  def getOperations(): OperationsGetDto = {
    val result = new OperationsGetDto
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

  @RequestMapping(value = Array[String]("/statement/operations"), method = Array[RequestMethod](RequestMethod.POST))
  def uploadOperations(@RequestBody entity: OperationsUpdateDto): OperationsUpdateResponseDto = {
    val userId = userIdProvider.getCurrentUserId
    val content = String.valueOf(Base64.getDecoder.decode(entity.base64Content).map(_.toChar))
    val operations = statementParser.parse(content)
    val toInsert = operations.filterNot(x => operationsRepository.operationExists(userId, x))

    toInsert.foreach(x => operationsRepository.insertOperation(userId, x))

    val result = new OperationsUpdateResponseDto
    result.duplicatesCount = operations.size - toInsert.size
    result.insertedCount = toInsert.size

    result
  }
}
