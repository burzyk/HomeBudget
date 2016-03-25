package com.jpbnetsoftware.homebudget.domain

/**
  * Created by pburzynski on 22/03/2016.
  */
trait CryptoHelper {
  def hash(password: String): String

  def decodeBase64(content: String): String

  def encodeBase64(content: String): String

  def encrypt(content: String, key: String): String

  def decrypt(content: String, key: String): String

  def getRandomKey(): String
}
