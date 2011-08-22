package c2c.webspecs
package geonetwork
package geocat

abstract class CreateSharedUser(user:User,validated:Boolean)
  extends AbstractFormPostRequest[Any,UserValue](
    (if(validated) "validated" else "nonvalidated")+".shared.user.update!",
    SelfValueFactory(),
    (P("operation", "newuser") :: SP("validated",if(validated) "y" else "no") :: user.formParams):_*)
  with ValueFactory[Any,UserValue] {

  override def createValue[A <: Any, B >: UserValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue,executionContext:ExecutionContext) = {
    new UserValue(user,rawValue) {
      override lazy val userId:String = {
        (ListUsers.valueFactory.createValue(ListUsers,in,rawValue,executionContext) find {_.username == user.username} map {_.userId}).get
      }
    }
  }
}

case class CreateValidatedUser(user:User) extends CreateSharedUser(user,true)
case class CreateNonValidatedUser(user:User) extends CreateSharedUser(user,false)