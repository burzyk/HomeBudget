package com.jpbnetsoftware.homebudget.service.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.beans.BeanProperty
;

/**
  * Created by pburzynski on 23/04/2016.
  */
class BalanceDetails(
                      @BeanProperty
                      var year: Int,

                      @BeanProperty
                      var month: Int,

                      @BeanProperty
                      var totalEarned: Double,

                      @BeanProperty
                      var totalSpent: Double) {
  @BeanProperty
  val balance = totalEarned - totalSpent
}
