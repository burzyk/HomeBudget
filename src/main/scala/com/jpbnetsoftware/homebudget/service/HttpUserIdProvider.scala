package com.jpbnetsoftware.homebudget.service

/**
  * Created by pburzynski on 22/03/2016.
  */
class HttpUserIdProvider extends UserIdProvider with AuthenticationManager {
  var userId: Int = _

  override def getCurrentUserId: Int = userId

  override def authenticate(authHeader: String): Boolean = {
    if (userId != 0) {
      throw new IllegalStateException("userId has already been set")
    }

    userId = authHeader.length
    true
  }
}
