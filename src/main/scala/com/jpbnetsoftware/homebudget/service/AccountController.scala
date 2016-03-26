package com.jpbnetsoftware.homebudget.service

import com.jpbnetsoftware.homebudget.data.UsersRepository
import com.jpbnetsoftware.homebudget.service.dto._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 26/03/2016.
  */
@RestController
class AccountController {

  @Autowired
  @BeanProperty
  var usersRepository: UsersRepository = _

  @RequestMapping(value = Array[String](UrlPaths.authenticateUrl), method = Array[RequestMethod](RequestMethod.POST))
  def authenticate(@RequestBody request: AccountAuthenticateRequest): AccountAuthenticateResponse = {
    new AccountAuthenticateResponse(usersRepository.userExists(request.username, request.password))
  }

  @RequestMapping(value = Array[String](UrlPaths.registerUrl), method = Array[RequestMethod](RequestMethod.POST))
  def register(@RequestBody request: AccountRegisterRequest): AccountRegisterResponse = {

    if (usersRepository.userExists(request.username)) {
      return new AccountRegisterResponse(false)
    }

    usersRepository.insertUser(request.username, request.password)
    new AccountRegisterResponse(true)
  }

}
