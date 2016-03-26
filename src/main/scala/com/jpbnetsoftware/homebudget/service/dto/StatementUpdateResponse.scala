package com.jpbnetsoftware.homebudget.service.dto

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class StatementUpdateResponse(
                                  @BeanProperty var insertedCount: Int,
                                  @BeanProperty var duplicatesCount: Int) {
}
