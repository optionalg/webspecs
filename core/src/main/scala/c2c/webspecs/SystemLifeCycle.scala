package c2c.webspecs

import util.control.Exception

/**
 * Implementation either take a config object as a parameter or no parameters
 */
trait SystemLifeCycle {
  def tearDown(implicit context:ExecutionContext):Unit
  def setup(implicit context:ExecutionContext):Unit
}

class NoActionLifeCycle extends SystemLifeCycle {
  def setup(implicit context:ExecutionContext) = ()
  def tearDown(implicit context:ExecutionContext) = ()
}

object SystemLifeCycle {
  def apply(config:Config):SystemLifeCycle = {
    Config.loadStrategy[SystemLifeCycle]("lifecycle").fold(
      e => {
        e.printStackTrace(System.err)
        throw e
      },
      i=>{
        val constructor = i.getConstructors.find{c =>
          c.getParameterTypes.length == 1 &&
          c.getParameterTypes.apply(0).isAssignableFrom(config.getClass())
        }
        val instance = constructor map {_.newInstance(config).asInstanceOf[SystemLifeCycle]}
        instance getOrElse {i.newInstance()}
      }
    )
  }
}