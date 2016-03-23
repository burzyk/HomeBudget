package com.jpbnetsoftware.homebudget.data.entities

import javax.persistence.{MappedSuperclass, Entity, Id, GeneratedValue}

import org.hibernate.annotations.GenericGenerator

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 23/03/2016.
  */
@MappedSuperclass
abstract class BaseEntity {
  @BeanProperty
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  var id: Int = _
}
