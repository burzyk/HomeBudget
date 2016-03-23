package com.jpbnetsoftware.homebudget.data

import javax.persistence.{EntityManager, Persistence}

import com.jpbnetsoftware.homebudget.data.entities.User
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

  override def operationExists(username: String, operation: BankOperation): Boolean = ???

  override def insertOperation(username: String, operation: BankOperation): Unit = ???

  override def getOperations(username: String): Map[Int, BankOperation] = ???

  override def userExists(username: String): Boolean = {
    dbOperation(x => {
      val result = x.createQuery("from User where username = :username", classOf[User])
        .setParameter("username", username)
        .getResultList()

      result.size() != 0
    })
  }

  override def userExists(username: String, password: String): Boolean = {
    dbOperation(x => {
      val result = x.createQuery("from User where username = :username and passwordHash = :passwordHash", classOf[User])
        .setParameter("username", username)
        .setParameter("passwordHash", cryptoHelper.hash(password))
        .getResultList()

      result.size() != 0
    })
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

  def dbOperation[T](x: EntityManager => T): T = {
    var result: T = null.asInstanceOf[T]

    val entityManagerFactory = EntityManagerCache.entityManagerFactory
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

  object EntityManagerCache {
    lazy val entityManagerFactory = Persistence.createEntityManagerFactory("com.jpbnetsoftware.homebudget.data")
  }

}
