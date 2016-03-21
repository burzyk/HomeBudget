package com.jpbnetsoftware.homebudget

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
  * Created by pburzynski on 21/03/2016.
  */
@SpringBootApplication
class Application {
  def startup(args: Array[String]): Unit = SpringApplication.run(Array[AnyRef](getClass), args)
}