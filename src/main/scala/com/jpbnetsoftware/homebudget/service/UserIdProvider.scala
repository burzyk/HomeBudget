package com.jpbnetsoftware.homebudget.service

/**
  * Created by pburzynski on 22/03/2016.
  */
trait UserIdProvider {
  def getCurrentUserId: Int
}
