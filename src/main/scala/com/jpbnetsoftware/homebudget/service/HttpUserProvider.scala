package com.jpbnetsoftware.homebudget.service

import com.jpbnetsoftware.homebudget.data.UsersRepository
import com.jpbnetsoftware.homebudget.domain.CryptoHelper
import org.springframework.beans.factory.annotation.Autowired

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 22/03/2016.
  */
class HttpUserProvider extends UserProvider with AuthenticationManager {

  @Autowired
  @BeanProperty
  var usersRepository: UsersRepository = _

  @Autowired
  @BeanProperty
  var cryptoHelper: CryptoHelper = _

  var username: String = _

  override def getCurrentUsername: String = username

  override def authenticate(authHeader: String): Boolean = {
    if (username != null) {
      throw new IllegalStateException("username has already been set")
    }

    if (authHeader == null) {
      return false
    }

    return authHeader.split(" ").toList match {
      case t :: v :: Nil if t == "Basic" => cryptoHelper.decodeBase64(v).split(":").toList match {
        case u :: p :: Nil if usersRepository.userExists(u, p) => username = u; true
        case _ => false
      }
      case _ => false
    }
  }

}
