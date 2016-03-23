package com.jpbnetsoftware.homebudget.data

import com.jpbnetsoftware.homebudget.domain.{CryptoHelper, BankOperation}
import org.springframework.beans.factory.annotation.Autowired

import scala.beans.BeanProperty
import scala.collection.mutable.HashMap

/**
  * Created by pburzynski on 21/03/2016.
  */
class InMemoryDataRepository extends OperationsRepository with UsersRepository {

  @Autowired
  @BeanProperty
  var cryptoHelper: CryptoHelper = _

  var operations: HashMap[String, List[BankOperation]] = HashMap[String, List[BankOperation]]()

  var users: List[User] = List[User]()

  override def operationExists(username: String, operation: BankOperation): Boolean = {
    operations.contains(username) && operations(username).count(_ == operation) != 0
  }

  override def insertOperation(username: String, operation: BankOperation): Unit = {
    if (!operations.contains(username)) {
      operations += (username -> List[BankOperation]())
    }

    operations(username) = operation :: operations(username)
  }

  override def getOperations(username: String): Map[Int, BankOperation] = {
    if (!operations.contains(username)) {
      Map[Int, BankOperation]()
    } else {
      operations(username).map(x => (operations(username).indexOf(x), x)).toMap
    }
  }

  override def insertUser(username: String, password: String): Int = {
    val id = users.length + 1
    users = new User(id, username, cryptoHelper.hash(password)) :: users

    id
  }

  override def userExists(username: String): Boolean = users.count(_.username == username) != 0

  override def userExists(username: String, password: String): Boolean = {
    users.count(x => x.username == username && x.passwordHash == cryptoHelper.hash(password)) != 0
  }

  class User(val id: Int, val username: String, val passwordHash: String) {
  }

}
