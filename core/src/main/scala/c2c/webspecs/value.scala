package c2c.webspecs


import xml.NodeSeq
import org.apache.http.Header
import util.control.Exception.allCatch
import java.net.URI

trait ValueFactory[-In,+Out] {
  def createValue[A <: In, B >: Out](request:Request[A,B],in:In,rawValue:BasicHttpValue,executionContext:ExecutionContext):Out
}

trait BasicValueFactory[Out] extends ValueFactory[Any,Out] {
  def createValue(rawValue:BasicHttpValue):Out
  def createValue[A <: Any, B >: Out](request:Request[A,B],
                            in:Any,
                            rawValue:BasicHttpValue,
                            executionContext:ExecutionContext):Out = createValue(rawValue)
}

object SelfValueFactory {
  def apply[In,Out]() = new ValueFactory[In,Out]{
    def createValue[A <: In, B >: Out](request:Request[A,B],in:In,rawValue:BasicHttpValue,executionContext:ExecutionContext):Out = {
      request.asInstanceOf[ValueFactory[In,Out]].createValue(request,in,rawValue,executionContext)
    }
  }
}
object XmlValueFactory extends BasicValueFactory[XmlValue] {
  def createValue(rawValue:BasicHttpValue) = rawValue.toXmlValue
}
object BasicHttpValue {
  def apply(executionResponse:ExecutionContext.Response):BasicHttpValue = {
    import executionResponse._
    import httpResponse._

    val contentLength = getEntity.getContentLength match {
      case x if x >= 0 => Some(x.toInt)
      case y => None
    }
    val responseCode = getStatusLine.getStatusCode
    val responseMessage = getStatusLine.getReasonPhrase

    val data = {
      try {
        import scalax.io.Input._
        val all = httpResponse.getEntity.getContent.asInput
        Right(all.byteArray)
      } catch {
        case e if responseCode > 400 => Left(new IllegalStateException("A response code "+responseCode+" was returned by server, message = "+responseMessage))
        case e => Left(e)
      }
    }
    val headers = getAllHeaders.
        map{_.getName}.
        map{name => (name.toLowerCase,httpResponse.getHeaders(name).toList)}.toMap
    BasicHttpValue(
      data,
      responseCode,
      responseMessage,
      headers,
      contentLength,
      Option(getEntity.getContentType) map {_.getValue},
      Option(getEntity.getContentEncoding) map {_.getValue},
      finalHost,
      finalURL
    )
  }
}
case class BasicHttpValue(data:Either[Throwable,Array[Byte]],
                          responseCode:Int,
                          responseMessage:String,
                          allHeaders: Map[String, List[Header]],
                          contentLength:Option[Int],
                          contentType:Option[String],
                          contentEncoding:Option[String],
                          finalHost:String,
                          finalURL:Option[URI]) {
  private val self = this

  def firstHeader(fieldName:String) = allHeaders.get(fieldName) flatMap {_.headOption}
  def toTextValue = new TextValue {
    def basicValue = self
  }
  def toXmlValue = new XmlValue {
    def basicValue = self
  }
}

trait TextValue {
  protected def basicValue:BasicHttpValue
  lazy val text = basicValue.data match {
    case Right(data) =>
      allCatch[String].either { new String(data, "UTF8") }
    case Left(error) => Left(error)
  }
  def withText[R](f:String => R):R = text.fold(throw _, f)
}
trait XmlValue extends TextValue {
  lazy val xml:Either[Throwable,NodeSeq] = text match {
    case Right(text) =>
      allCatch[NodeSeq].either {
        val xml = TagSoupFactoryAdapter.loadString(text)

        val error = xml \\ "ExceptionReport" \ "Exception" \ "ExceptionText"
        if(error.nonEmpty) {
          throw new IllegalStateException("Server response contained ExceptionReport: "+error.text.replace("&lt;","<").replace("&gt;",">"))
        }
        xml
      }
    case Left(error) => Left(error)
  }

  def withXml[R](f:NodeSeq => R):R = xml.fold(throw _, f)
}

object Id {
  def apply(idString:String) = new Id {
    val id = idString
  }
}
trait Id {
  def id:String
}
object IdValue {
  def apply(in:String,rawValue:BasicHttpValue) = new IdValue {
    protected def basicValue = rawValue
    def id = in
  }
}
trait IdValue extends XmlValue with Id

case class ExplicitIdValueFactory(idVal:String) extends BasicValueFactory[IdValue]{
  def createValue(rawValue: BasicHttpValue) = new XmlValue with IdValue {
     val basicValue = rawValue
     val id = idVal
  }
}

case class PassThroughValueFactory[A]() extends ValueFactory[A,A] {
  def createValue[B <: A, C >: A]
                 (request: Request[B, C],
                  in: A,
                  rawValue:
                  BasicHttpValue,
                  executionContext: ExecutionContext) = in
}

case class InputTransformerValueFactory[A,B](f:(A,BasicHttpValue) => B) extends ValueFactory[A,B] {
    def createValue[C <: A, D >: B]
                 (request: Request[C, D],
                  in: A,
                  rawValue:BasicHttpValue,
                  executionContext: ExecutionContext) = f(in,rawValue)
}