package com.jpbnetsoftware.homebudget.data

import javax.persistence.Persistence

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

  override def getOperations(username: String): List[BankOperation] = ???

  override def userExists(username: String): Boolean = ???

  override def userExists(username: String, password: String): Boolean = {
    val entityManagerFactory = EntityManagerCache.entityManagerFactory

    val entityManager = entityManagerFactory.createEntityManager()

    entityManager.getTransaction().begin()
    val result = entityManager.createQuery("from User where username = :username and passwordHash = :passwordHash", classOf[User])
      .setParameter("username", username)
      .setParameter("passwordHash", cryptoHelper.hash(password))
      .getResultList()

    entityManager.getTransaction().commit()
    entityManager.close()

    result.size() != 0
  }

  override def insertUser(username: String, password: String): Int = {
    val entityManagerFactory = EntityManagerCache.entityManagerFactory

    val entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();

    val user = new User()
    user.username = username
    user.passwordHash = cryptoHelper.hash(password);

    entityManager.persist(user);

    entityManager.getTransaction().commit();
    entityManager.close();

    user.id
  }

  object EntityManagerCache {
    lazy val entityManagerFactory = Persistence.createEntityManagerFactory("com.jpbnetsoftware.homebudget.data")
  }

}
