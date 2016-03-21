package com.jpbnetsoftware.homebudget.domain

/**
  * Created by pburzynski on 21/03/2016.
  */
trait StatementParser {
  def parse(content: String): Seq[BankOperation]
}
