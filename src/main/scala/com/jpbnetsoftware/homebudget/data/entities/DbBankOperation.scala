package com.jpbnetsoftware.homebudget.data.entities

import java.time.LocalDate

import com.jpbnetsoftware.homebudget.domain.BankOperation

/**
  * Created by pburzynski on 07/05/2016.
  */
class DbBankOperation(var id: Int, date: LocalDate, description: String, amount: Double)
  extends BankOperation(date, description, amount) {
}
