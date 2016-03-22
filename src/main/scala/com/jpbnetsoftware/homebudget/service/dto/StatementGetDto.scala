package com.jpbnetsoftware.homebudget.service.dto

import scala.beans.BeanProperty
import scala.collection.immutable.List

/**
  * Created by pburzynski on 21/03/2016.
  */
class StatementGetDto {
  @BeanProperty var operations: java.util.List[OperationDetailsDto] = _
}
