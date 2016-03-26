package com.jpbnetsoftware.homebudget.service.dto

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 26/03/2016.
  */
class AccountAuthenticateRequest {
  @BeanProperty var username: String = _
  @BeanProperty var password: String = _
}
