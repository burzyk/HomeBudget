package com.jpbnetsoftware.homebudget.data

import com.jpbnetsoftware.homebudget.domain.BankOperation

/**
  * Created by pburzynski on 21/03/2016.
  */
trait OperationsRepository {
  def operationExists(userId: Int, operation: BankOperation): Boolean

  def insertOperation(userId: Int, operation: BankOperation): Unit

  def getOperations(userId: Int): List[BankOperation]
}
