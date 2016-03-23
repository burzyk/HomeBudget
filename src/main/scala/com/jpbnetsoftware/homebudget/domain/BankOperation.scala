package com.jpbnetsoftware.homebudget.domain

import java.time.LocalDate


/**
  * Created by pburzynski on 21/03/2016.
  */
case class BankOperation(var date: LocalDate, var description: String, var amount: Double) {
}
