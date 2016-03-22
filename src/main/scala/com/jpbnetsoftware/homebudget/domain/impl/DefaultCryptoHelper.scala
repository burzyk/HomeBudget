package com.jpbnetsoftware.homebudget.domain.impl

import java.security.MessageDigest
import java.util.Base64

import com.jpbnetsoftware.homebudget.domain.CryptoHelper

/**
  * Created by pburzynski on 22/03/2016.
  */
class DefaultCryptoHelper extends CryptoHelper {
  override def decodeBase64(content: String): String = String.valueOf(Base64.getDecoder.decode(content).map(_.toChar))

  override def hash(password: String): String = {
    val digest = MessageDigest.getInstance("SHA-512")
    String.valueOf(Base64.getEncoder.encode(digest.digest(password.getBytes("UTF-8"))).map(_.toChar))
  }
}
