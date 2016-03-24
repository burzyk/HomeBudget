package com.jpbnetsoftware.homebudget.domain

/**
  * Created by pburzynski on 22/03/2016.
  */
trait CryptoHelper {
  def hash(password: String): String

  def decodeBase64(content: String): String

  def encodeBase64(content: String): String
}
