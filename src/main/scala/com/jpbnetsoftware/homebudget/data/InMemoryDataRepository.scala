package com.jpbnetsoftware.homebudget.data

import com.jpbnetsoftware.homebudget.domain.BankOperation

import scala.collection.mutable.HashMap

/**
  * Created by pburzynski on 21/03/2016.
  */
class InMemoryDataRepository extends OperationsRepository with UsersRepository {

  var operations: HashMap[Int, List[BankOperation]] = HashMap[Int, List[BankOperation]]()

  var users: List[(Int, String)] = List[(Int, String)]()

  override def operationExists(userId: Int, operation: BankOperation): Boolean = {
    operations.contains(userId) && operations(userId).count(_ == operation) != 0
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

  override def getUserIdByUsername(username: String): Int = users.find(_._2 == username).get._1

  override def insertUser(username: String, password: String): Int = {
    val id = users.length
    users = (id, username) :: users

    id
  }

  override def userExists(username: String): Boolean = users.count(_._1 == username) != 0
}
