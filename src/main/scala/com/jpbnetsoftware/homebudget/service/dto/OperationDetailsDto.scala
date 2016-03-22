package com.jpbnetsoftware.homebudget.service.dto

import java.time.LocalDate

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class OperationDetailsDto {
  @BeanProperty var id: Int = _
  @BeanProperty var date: LocalDate = _
  @BeanProperty var description: String = _
  @BeanProperty var amount: Double = _
}
