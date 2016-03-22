package com.jpbnetsoftware.homebudget.data

import com.jpbnetsoftware.homebudget.domain.BankOperation

/**
  * Created by pburzynski on 21/03/2016.
  */
trait OperationsRepository {
  def operationExists(username: String, operation: BankOperation): Boolean

  def insertOperation(username: String, operation: BankOperation): Unit

  def getOperations(username: String): List[BankOperation]
}
