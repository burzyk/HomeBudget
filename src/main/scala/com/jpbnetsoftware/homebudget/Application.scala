package com.jpbnetsoftware.homebudget

import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

/**
  * Created by pburzynski on 21/03/2016.
  */

object Application {
  var applicationContext: ConfigurableApplicationContext = _

  def main(args: Array[String]): Unit = {
    applicationContext = SpringApplication.run(Array[AnyRef](classOf[AppConfig]), args)
  }
}