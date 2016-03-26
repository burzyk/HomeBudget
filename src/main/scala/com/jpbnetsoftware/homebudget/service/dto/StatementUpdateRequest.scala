package com.jpbnetsoftware.homebudget.service.dto

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class StatementUpdateRequest {
  @BeanProperty var base64QifOperations: String = _
}
