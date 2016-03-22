package com.jpbnetsoftware.homebudget

import javax.servlet.Filter

import com.jpbnetsoftware.homebudget.data.{OperationsRepository, InMemoryOperationsRepository}
import com.jpbnetsoftware.homebudget.domain.StatementParser
import com.jpbnetsoftware.homebudget.impl.QifStatementParser
import com.jpbnetsoftware.homebudget.service._
import org.apache.commons.logging.{LogFactory, Log}
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.{ConfigurableApplicationContext, ApplicationContext}
import org.springframework.context.annotation.{Scope, ScopedProxyMode, Bean}

/**
  * Created by pburzynski on 21/03/2016.
  */
@SpringBootApplication
class AppConfig {

  @Bean
  def operationsRepository(): OperationsRepository = new InMemoryOperationsRepository()

  @Bean
  def statementParser(): StatementParser = new QifStatementParser()

  @Bean
  def appSecurityFilter(): Filter = new AppSecurityFilter

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def userIdProvider(): UserIdProvider = {
    Application.applicationContext.getBean("httpUserIdProvider").asInstanceOf[UserIdProvider]
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def authenticationManager(): AuthenticationManager = {
    Application.applicationContext.getBean("httpUserIdProvider").asInstanceOf[AuthenticationManager]
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def httpUserIdProvider(): HttpUserIdProvider = new HttpUserIdProvider
}