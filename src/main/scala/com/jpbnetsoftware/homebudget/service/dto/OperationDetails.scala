package com.jpbnetsoftware.homebudget.service.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
class OperationDetails(
                        @BeanProperty
                        var id: Int,

                        var fullDate: LocalDate,

                        @BeanProperty
                        var description: String,

                        @BeanProperty
                        var amount: Double) {
  @BeanProperty
  val date = fullDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
