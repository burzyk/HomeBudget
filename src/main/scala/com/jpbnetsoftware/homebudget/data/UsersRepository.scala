package com.jpbnetsoftware.homebudget.data

/**
  * Created by pburzynski on 22/03/2016.
  */
trait UsersRepository {
  def getUserIdByUsername(username: String): Int

  def insertUser(username: String, password: String): Int

  def userExists(username: String): Boolean
}
