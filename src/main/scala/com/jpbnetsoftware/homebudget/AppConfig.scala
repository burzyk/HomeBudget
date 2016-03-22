package com.jpbnetsoftware.homebudget

import javax.servlet.Filter

import com.jpbnetsoftware.homebudget.data.{InMemoryDataRepository, OperationsRepository, UsersRepository}
import com.jpbnetsoftware.homebudget.domain.StatementParser
import com.jpbnetsoftware.homebudget.impl.QifStatementParser
import com.jpbnetsoftware.homebudget.service._
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Scope, ScopedProxyMode}

/**
  * Created by pburzynski on 21/03/2016.
  */
@SpringBootApplication
class AppConfig {

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def operationsRepository(): OperationsRepository = {
    Application.applicationContext.getBean("dataRepository").asInstanceOf[OperationsRepository]
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def usersRepository(): UsersRepository = {
    Application.applicationContext.getBean("dataRepository").asInstanceOf[UsersRepository]
  }

  @Bean
  def dataRepository(): InMemoryDataRepository = new InMemoryDataRepository()

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