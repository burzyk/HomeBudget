package com.jpbnetsoftware.homebudget.domain

import java.time.LocalDate


/**
  * Created by pburzynski on 21/03/2016.
  */
class BankOperation(var id: Int, var date: LocalDate, var description: String, var amount: Double) {
  override def equals(o: Any) = o match {
    case that: BankOperation => that.date == date && that.description == description && that.amount == amount
    case _ => false
  }

  override def hashCode = (date.toString + description + amount.toString).hashCode
}
