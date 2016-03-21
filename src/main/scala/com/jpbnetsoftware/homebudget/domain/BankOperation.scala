package com.jpbnetsoftware.homebudget.domain

import java.util.Date

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class BankOperation {
  @BeanProperty var date: Date = _
  @BeanProperty var description: String = _
  @BeanProperty var amount: Double = _
}
