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

  override def getOperations(username: String, from: LocalDate, to: LocalDate): Map[Int, BankOperation] = {
    def validateDate(date: LocalDate) = date.isAfter(LocalDate.of(1900, 1, 1)) && date.isBefore(LocalDate.of(3000, 1, 1))

    if (!validateDate(from)) {
      throw new IllegalArgumentException("from")
    }

    if (!validateDate(to)) {
      throw new IllegalArgumentException("to")
    }

    dbOperation(x => {
      val result = x.createQuery(
        "from EncryptedBankOperation where " +
          "user.username = :username and :from <= date and date <= :to " +
          "order by date desc",
        classOf[EncryptedBankOperation])
        .setParameter("username", username)
        .setParameter("from", Date.valueOf(from))
        .setParameter("to", Date.valueOf(to))
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

