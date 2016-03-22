package com.jpbnetsoftware.homebudget.domain.impl

import java.util.Base64

import com.jpbnetsoftware.homebudget.domain.CryptoHelper

/**
  * Created by pburzynski on 22/03/2016.
  */
class DefaultCryptoHelper extends CryptoHelper {
  override def decodeBase64(content: String): String = String.valueOf(Base64.getDecoder.decode(content).map(_.toChar))

  override def hash(password: String): String = ???
}
