package com.jpbnetsoftware.homebudget

import org.springframework.web.bind.annotation.{RequestMapping, RestController}

/**
  * Created by pburzynski on 21/03/2016.
  */
@RestController
class SimpleController {

  @RequestMapping(Array[String]("/test-method"))
  def testMethod() = "ala ma kota"
}
