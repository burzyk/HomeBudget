package com.jpbnetsoftware.homebudget.service

/**
  * Created by pburzynski on 22/03/2016.
  */
trait AuthenticationManager {
  def authenticate(authHeader: String): Boolean
}
