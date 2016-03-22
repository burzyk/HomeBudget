package com.jpbnetsoftware.homebudget.service.dto

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class StatementUpdateResponseDto {
  @BeanProperty var insertedCount: Int = _
  @BeanProperty var duplicatesCount: Int = _
}
