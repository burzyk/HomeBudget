package com.jpbnetsoftware.homebudget.impl

import java.time.{ZoneOffset, LocalDateTime, LocalDate, Instant}
import java.time.format.DateTimeFormatter

import com.jpbnetsoftware.homebudget.domain.{BankOperation, StatementParserException, StatementParser}

/**
  * Created by pburzynski on 21/03/2016.
  */
class QifStatementParser extends StatementParser {
  override def parse(content: String): Seq[BankOperation] = {
    def parseDate(value: String): LocalDate = {
      LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(value))
    }

    val operations = extractRecords(content).map(x => {
      val operation = new BankOperation
      operation.date = parseDate(safeGetRecord(x, 'D'))
      operation.description = safeGetRecord(x, 'P')
      operation.amount = safeGetRecord(x, 'T').toDouble

      operation
    })

    operations.sortWith((o1, o2) => o1.date.isBefore(o2.date))
  }

  private def safeGetRecord(records: Map[Char, String], recordId: Char): String = {
    if (!records.contains(recordId)) {
      throw new StatementParserException("Missing recordId: " + recordId)
    }

    records(recordId)
  }

  private def extractRecords(content: String): List[Map[Char, String]] = {
    content
      .split("\\^")
      .map(x => x.split("[\\r\\n]+").map(_.trim))
      .map(x => x
        .filterNot(_.startsWith("!"))
        .filterNot(_.isEmpty)
        .map(l => (l.charAt(0) -> l.substring(1)))
        .toMap)
      .filterNot(_.isEmpty)
      .toList
  }
}
