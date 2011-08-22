package c2c.webspecs
package geonetwork
package geocat

abstract class UpdateSharedUser(val user:User, validated:Boolean)
  extends AbstractFormPostRequest[Any,UserValue](
      (if(validated) "validated" else "nonvalidated")+".shared.user.update", 
      SelfValueFactory(), 
      P("operation", "fullupdate") :: SP("validated",if(validated) "y" else "no") :: user.formParams() : _*)
  with ValueFactory[Any,UserValue] {

  def createValue[A <: Any, B >: UserValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue,executionContext:ExecutionContext) = {
    new UserValue(user.copy(idOption = Some(user.idOption.get)),rawValue)
  }
}
