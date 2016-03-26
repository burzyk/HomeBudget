package com.jpbnetsoftware.homebudget.service.dto

import java.time.LocalDate

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class OperationDetails(
                           @BeanProperty var id: Int,
                           @BeanProperty var date: LocalDate,
                           @BeanProperty var description: String,
                           @BeanProperty var amount: Double) {
}
