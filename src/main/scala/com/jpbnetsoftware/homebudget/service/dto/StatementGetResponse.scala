package com.jpbnetsoftware.homebudget.service.dto

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class StatementGetResponse(@BeanProperty var operations: java.util.List[OperationDetails]) {
}
