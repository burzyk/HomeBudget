package com.jpbnetsoftware.homebudget.domain.impl

import java.security.{SecureRandom, MessageDigest}
import java.time.LocalDateTime
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import com.jpbnetsoftware.homebudget.domain.CryptoHelper

/**
  * Created by pburzynski on 22/03/2016.
  */
class DefaultCryptoHelper extends CryptoHelper {

  val defaultEncoding = "UTF-8"

  override def decodeBase64(content: String): String = new String(decodeBase64ToBytes(content), defaultEncoding)

  override def hash(password: String): String = {
    val digest = MessageDigest.getInstance("SHA-512")
    encodeBase64ToString(digest.digest(password.getBytes(defaultEncoding)))
  }

  override def encodeBase64(content: String): String = {
    return encodeBase64ToString(content.getBytes(defaultEncoding))
  }

  override def encrypt(content: String, key: String): String = {
    val cipher = getCipher
    val iv = getRandomBytes(cipher.getBlockSize)

    cipher.init(Cipher.ENCRYPT_MODE, getKeySpec(key), new IvParameterSpec(iv))

    encodeBase64ToString(iv) + ":" + encodeBase64ToString(cipher.doFinal(content.getBytes()))
  }

  override def decrypt(content: String, key: String): String = {
    val cipher = getCipher

    content.split(":").toList match {
      case iv :: c :: Nil => {
        cipher.init(Cipher.DECRYPT_MODE, getKeySpec(key), new IvParameterSpec(decodeBase64ToBytes(iv)))
        new String(cipher.doFinal(decodeBase64ToBytes(c)), defaultEncoding)
      }
      case _ => throw new UnsupportedOperationException
    }
  }

  def getCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

  def getKeySpec(key: String) = new SecretKeySpec(sha256(key), "AES")

  def getRandomBytes(count: Int): Array[Byte] = {
    val randomSecureRandom = SecureRandom.getInstance("SHA1PRNG")
    val random = new Array[Byte](count)
    randomSecureRandom.nextBytes(random)

    random
  }

  def sha256(key: String): Array[Byte] = {
    MessageDigest.getInstance("SHA-256").digest(key.getBytes("UTF-8"))
  }

  def decodeBase64ToBytes(content: String): Array[Byte] = {
    Base64.getDecoder.decode(content)
  }

  def encodeBase64ToString(content: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(content);
  }
}
