package com.jpbnetsoftware.homebudget.data

import com.jpbnetsoftware.homebudget.domain.BankOperation

import scala.collection.mutable.HashMap

/**
  * Created by pburzynski on 21/03/2016.
  */
class InMemoryOperationsRepository extends OperationsRepository {

  var operations: HashMap[Int, List[BankOperation]] = HashMap[Int, List[BankOperation]]()

  override def operationExists(userId: Int, operation: BankOperation): Boolean = {
    operations.contains(userId) && operations(userId).count(o => o == operation) != 0
  }

  override def insertOperation(userId: Int, operation: BankOperation): Unit = {
    if (!operations.contains(userId)) {
      operations += (userId -> List[BankOperation]())
    }

    operations(userId) = operation :: operations(userId)
  }

  override def getOperations(userId: Int): List[BankOperation] = {
    if (!operations.contains(userId)) List[BankOperation]() else operations(userId)
  }
}
