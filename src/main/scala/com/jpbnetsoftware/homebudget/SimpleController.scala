package com.jpbnetsoftware.homebudget

import org.springframework.web.bind.annotation._
import scala.beans.BeanProperty

/**
  * Created by pburzynski on 21/03/2016.
  */
@RestController
class SimpleController {

  @RequestMapping(Array[String]("/test-method"))
  def testMethod(): Message = {
    val msg = new Message
    msg.id = 12
    msg.text = "ala ma kota"

    msg
  }

  @RequestMapping(value = Array[String]("/test-method"), method = Array[RequestMethod](RequestMethod.POST))
  def increment(@RequestBody msg: Message): Message = {
    msg.id = msg.id + 1

    msg
  }
}

class Message {
  @BeanProperty var id: Int = _
  @BeanProperty var text: String = _
}
