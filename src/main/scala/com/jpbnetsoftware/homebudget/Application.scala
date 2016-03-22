package com.jpbnetsoftware.homebudget

import com.jpbnetsoftware.homebudget.data.{OperationsRepository, InMemoryOperationsRepository}
import com.jpbnetsoftware.homebudget.domain.StatementParser
import com.jpbnetsoftware.homebudget.impl.QifStatementParser
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

/**
  * Created by pburzynski on 21/03/2016.
  */
@SpringBootApplication
class Application {
  def startup(args: Array[String]): Unit = SpringApplication.run(Array[AnyRef](getClass), args)

  @Bean
  def getOperationsRepository(): OperationsRepository = new InMemoryOperationsRepository()

  @Bean
  def getStatementParser(): StatementParser = new QifStatementParser()
}