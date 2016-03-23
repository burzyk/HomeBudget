package com.jpbnetsoftware.homebudget

import javax.servlet.Filter

import com.jpbnetsoftware.homebudget.data.{HibernateRepository, OperationsRepository, UsersRepository}
import com.jpbnetsoftware.homebudget.domain.{CryptoHelper, StatementParser}
import com.jpbnetsoftware.homebudget.domain.impl.{DefaultCryptoHelper, QifStatementParser}
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
  def cryptoHelper(): CryptoHelper = new DefaultCryptoHelper()

  @Bean
  def dataRepository(): HibernateRepository = new HibernateRepository()

  @Bean
  def statementParser(): StatementParser = new QifStatementParser()

  @Bean
  def appSecurityFilter(): Filter = new AppSecurityFilter

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def userProvider(): UserProvider = {
    Application.applicationContext.getBean("httpUserProvider").asInstanceOf[UserProvider]
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def authenticationManager(): AuthenticationManager = {
    Application.applicationContext.getBean("httpUserProvider").asInstanceOf[AuthenticationManager]
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  def httpUserProvider(): HttpUserProvider = new HttpUserProvider
}