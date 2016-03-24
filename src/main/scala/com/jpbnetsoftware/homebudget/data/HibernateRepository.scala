package com.jpbnetsoftware.homebudget.data

import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.persistence.{EntityManager, Persistence}

import com.jpbnetsoftware.homebudget.data.entities.{EncryptedBankOperation, User}
import com.jpbnetsoftware.homebudget.domain.{CryptoHelper, BankOperation}
import org.springframework.beans.factory.annotation.Autowired

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 23/03/2016.
  */
class HibernateRepository extends OperationsRepository with UsersRepository {

  @Autowired
  @BeanProperty
  var cryptoHelper: CryptoHelper = _

  def entityManagerFactory = EntityManagerProvider.entityManagerFactory

  override def operationExists(username: String, operation: BankOperation): Boolean = {
    dbOperation(x => {
      val result = x.createQuery(
        "from EncryptedBankOperation where " +
          "user.username = :username and " +
          "date = :date and " +
          "description = :description and " +
          "amount = :amount", classOf[EncryptedBankOperation])
        .setParameter("username", username)
        .setParameter("date", Date.valueOf(operation.date))
        .setParameter("description", operation.description)
        .setParameter("amount", operation.amount.toString)
        .getResultList()

      result.size() != 0
    })
  }

  override def insertOperation(username: String, operation: BankOperation): Unit = {
    dbOperation(x => {
      val dbOperation = new EncryptedBankOperation()

      dbOperation.date = Date.valueOf(operation.date)
      dbOperation.description = operation.description
      dbOperation.amount = operation.amount.toString
      dbOperation.user = getUser(username)

      x.persist(dbOperation)
    })
  }

  override def getOperations(username: String): Map[Int, BankOperation] = {
    dbOperation(x => {
      val result = x.createQuery(
        "from EncryptedBankOperation where user.username = :username order by date",
        classOf[EncryptedBankOperation])
        .setParameter("username", username)
        .getResultList()

      result.toArray
        .map(_.asInstanceOf[EncryptedBankOperation])
        .map(x => (x.id -> new BankOperation(x.date.toLocalDate(), x.description, x.amount.toDouble)))
        .toMap
    })
  }

  override def userExists(username: String): Boolean = {
    getUser(username) != null
  }

  override def userExists(username: String, password: String): Boolean = {
    val user = getUser(username)
    user != null && user.passwordHash == cryptoHelper.hash(password)
  }

  override def insertUser(username: String, password: String): Int = {
    dbOperation(x => {
      val user = new User()

      user.username = username
      user.passwordHash = cryptoHelper.hash(password)

      x.persist(user)

      user.id
    })
  }

  def getUser(username: String): User = {
    dbOperation(x => {
      val result = x.createQuery("from User where username = :username", classOf[User])
        .setParameter("username", username)
        .getResultList()

      val users = result.toArray.map(_.asInstanceOf[User])
      if (users.size == 1) users.apply(0) else null
    })
  }

  def dbOperation[T](x: EntityManager => T): T = {
    var result: T = null.asInstanceOf[T]

    val entityManager = entityManagerFactory.createEntityManager()

    try {
      entityManager.getTransaction().begin()

      result = x(entityManager)

      entityManager.getTransaction().commit()
    }
    finally {
      entityManager.close()
    }

    result
  }
}

