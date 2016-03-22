package com.jpbnetsoftware.homebudget.data

/**
  * Created by pburzynski on 22/03/2016.
  */
trait UsersRepository {
  def insertUser(username: String, password: String): Int

  def userExists(username: String): Boolean

  def userExists(username: String, password: String): Boolean
}
