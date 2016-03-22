package com.jpbnetsoftware.homebudget.data

import com.jpbnetsoftware.homebudget.domain.BankOperation

/**
  * Created by pburzynski on 21/03/2016.
  */
class InMemoryOperationsRepository extends OperationsRepository {

  var operations: List[BankOperation] = List[BankOperation]()

  override def operationExists(userId: Int, operation: BankOperation): Boolean = {
    operations.count(o => o == operation) != 0
  }

  override def insertOperation(userId: Int, operation: BankOperation): Unit = {
    operations = operation :: operations
  }

  override def getOperations(userId: Int): List[BankOperation] = {
    operations
  }
}
