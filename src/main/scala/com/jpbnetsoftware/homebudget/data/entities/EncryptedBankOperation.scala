package com.jpbnetsoftware.homebudget.data.entities

import java.sql.Date
import javax.persistence.{ManyToOne, Entity, Column}

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 24/03/2016.
  */
@Entity
class EncryptedBankOperation extends BaseEntity {

  @BeanProperty
  @ManyToOne(optional = false)
  var user: User = _

  @BeanProperty
  @Column
  var date: Date = _

  @BeanProperty
  @Column
  var description: String = _

  @BeanProperty
  @Column
  var amount: String = _
}
