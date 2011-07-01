package c2c.webspecs

import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.client.methods.HttpPost

abstract class AbstractMultiPartFormRequest[In,Out](url:String, valueFactory:ValueFactory[In,Out], form:Param[In,ContentBody]*)
  extends AbstractRequest[In,Out](valueFactory) {

  require(form.size > 0, "At least one form element is required")

   def request(in:In) = {
     val httppost = new HttpPost(Config.resolveURI(url))

     val reqEntity = new MultipartEntity()
     form.foreach {part =>
       {
         val name: String = part.name
         val contentBody: ContentBody = part.value(in)
         reqEntity.addPart(name, contentBody)
       }
     }
     httppost.setEntity(reqEntity)

     httppost
   }
}

case class MultiPartFormRequest(url:String, form:(String,ContentBody)*)
  extends AbstractMultiPartFormRequest[Any,XmlValue](url,XmlValueFactory,form.map(p => P(p._1,p._2)):_*)