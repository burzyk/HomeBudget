package com.jpbnetsoftware.homebudget.domain

/**
  * Created by pburzynski on 25/03/2016.
  */
class InvalidKeyException(inner: Exception) extends Exception("Invalid Key", inner) {
}
