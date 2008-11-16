package com.yuroyoro.liftropy.model

import net.liftweb.mapper._

object TropyEntry extends TropyEntry with KeyedMetaMapper[Long,TropyEntry]{
}

class TropyEntry extends KeyedMapper[Long ,TropyEntry]{

  def getSingleton = TropyEntry
  def primaryKeyField = tropyId
  
  object tropyId extends MappedLongIndex(this)
  
  object entry extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }
  
}