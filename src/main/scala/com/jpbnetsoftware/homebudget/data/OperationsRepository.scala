package com.jpbnetsoftware.homebudget.data

import java.time.LocalDate

import com.jpbnetsoftware.homebudget.data.entities.DbBankOperation
import com.jpbnetsoftware.homebudget.domain.BankOperation

/**
  * Created by pburzynski on 21/03/2016.
  */
trait OperationsRepository {
  def insertOperations(username: String, password: String, operations: Seq[BankOperation]): Unit

  def getOperations(username: String, password: String, from: LocalDate, to: LocalDate): List[DbBankOperation]

  def getOperations(username: String, password: String, index: Int, count: Int): List[DbBankOperation]

  def getOperations(username: String, password: String, from: LocalDate, to: LocalDate, index: Int, count: Int): List[DbBankOperation]
}
