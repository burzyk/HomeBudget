package com.jpbnetsoftware.homebudget.data

import javax.persistence.Persistence

/**
  * Created by pburzynski on 23/03/2016.
  */
object EntityManagerProvider {
  def createEntityManagerFactory = Persistence.createEntityManagerFactory("com.jpbnetsoftware.homebudget.data")

  lazy val entityManagerFactory = createEntityManagerFactory
}

