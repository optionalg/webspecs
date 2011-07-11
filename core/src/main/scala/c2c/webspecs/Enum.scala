package c2c.webspecs

import java.util.concurrent.atomic.AtomicReference
import java.util.ServiceLoader
import collection.JavaConverters._
import java.util.concurrent.atomic.AtomicReference._

/**
 * An enumeration super class
 */

trait Enum  {

  type EnumVal <: Value //This is a type that needs to be found in the implementing class
  def pluginClass : Class[_]

  private val _values = new AtomicReference(Vector[EnumVal]()) //Stores our enum values

  //Adds an EnumVal to our storage, uses CCAS to make sure it's thread safe, returns the ordinal
  private final def addEnumVal(newVal: EnumVal): Int = {
    import _values.{get,compareAndSet}

    val oldVec = get
    val newVec = oldVec :+ newVal
    if((get eq oldVec) && compareAndSet(oldVec, newVec)) newVec.indexWhere(_ eq newVal) else addEnumVal(newVal)
  }

  def values: Vector[EnumVal] = _values.get //Here you can get all the enums that exist for this type

  def withName(name:String) = values find {_.allNames contains name}

  //This is the trait that we need to extend our EnumVal type with, it does the book-keeping for us
  protected trait Value { self: EnumVal => //Enforce that no one mixes in Value in a non-EnumVal type
    final val ordinal = addEnumVal(this) //Adds the EnumVal and returns the ordinal

    def name: String //All enum values should have a name
    def aliases:Seq[String] = Nil
    lazy val allNames = name +: aliases

    override def toString = name //And that name is used for the toString operation
    override def equals(other: Any) = other.asInstanceOf[AnyRef] eq this
    override def hashCode = 31 * (this.getClass.## + name.## + ordinal)
  }


  def delayedInit(x: => Unit) {
 //   ServiceLoader.load(pluginClass).asScala.foreach{ext => Log.apply(Log.Plugins,"Loading enumeration value: "+ext)}
  }
}