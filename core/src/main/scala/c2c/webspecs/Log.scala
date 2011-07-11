package c2c.webspecs

import java.util.logging.{ConsoleHandler, Logger}

trait Log {

  // To show httpclient logging run application with:
  // -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog -Dorg.apache.commons.logging.simplelog.showdatetime=true -Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG -Dorg.apache.commons.logging.simplelog.log.org.apache.http.wire=ERROR
  private object LoggingConfig {
    val all = false
    val enabled = Error :: Warning :: RequestXml :: RequestForm :: Connection :: Headers :: LifeCycle :: Nil

  }

  sealed trait Level
  case object RequestXml extends Level
  case object Connection extends Level
  case object Headers extends Level
  case object RequestForm extends Level
  case object RequestMPForm extends Level
  case object LifeCycle extends Level
  case object Constants extends Level
  case object Warning extends Level
  case object Error extends Level
  case object Plugins extends Level

  protected def log(level:Level, msg: => Any) = {
    if(LoggingConfig.all || LoggingConfig.enabled.contains(level)) {
      write(msg)
    }
  }

  private def write(msg:Any) = System.err.println(msg)

}

object Log extends Log {
  def apply(level:Level, msg: => Any) = if(LoggingConfig.all || LoggingConfig.enabled.contains(level)) { write(msg) }
}
