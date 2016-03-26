package com.jpbnetsoftware.homebudget.data.entities

import javax.persistence.{Column, Entity}

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 23/03/2016.
  */
@Entity
class User extends BaseEntity {
  @BeanProperty
  @Column(unique = true)
  var username: String = _

  @BeanProperty
  @Column
  var passwordHash: String = _

  @BeanProperty
  @Column(unique = true)
  var encryptionKey: String = _
}
