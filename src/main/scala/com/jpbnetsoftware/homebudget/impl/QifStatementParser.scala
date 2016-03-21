package com.jpbnetsoftware.homebudget.impl

import com.jpbnetsoftware.homebudget.domain.{BankOperation, StatementParser}

/**
  * Created by pburzynski on 21/03/2016.
  */
class QifStatementParser extends StatementParser {
  override def parse(content: String): Seq[BankOperation] = ???
}
