package com.jpbnetsoftware.homebudget.data

import java.time.LocalDate

import com.jpbnetsoftware.homebudget.domain.BankOperation

/**
  * Created by pburzynski on 21/03/2016.
  */
trait OperationsRepository {
  def insertOperations(username: String, password: String, operations: Seq[BankOperation]): Unit

  def getOperations(username: String, password: String, from: LocalDate, to: LocalDate): Map[Int, BankOperation]
}
