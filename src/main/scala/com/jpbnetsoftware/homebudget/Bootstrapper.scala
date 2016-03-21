package com.jpbnetsoftware.homebudget

/**
  * Created by pburzynski on 21/03/2016.
  */

object Bootstrapper {
  def main(args: Array[String]): Unit = {
    val app = new Application
    app.startup(args)
  }
}