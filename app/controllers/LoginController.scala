package controllers

import play.api.Logger
import play.api.mvc.Action
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import repo.{DB, SiteRepoImpl}
/**
  * Created by Oleksii on 25.05.2017.
  */
object LoginController extends Controller {
  def auth = Action {
    Ok("authenticated").withSession("otp"->"dd","time"->System.currentTimeMillis().toString)
  }
  def verify=Action{request=>
    request.session.get("otp").map(user=>{
      var z=System.currentTimeMillis()-request.session.get("time").get.toLong
      print("OLOLO")
      print(z)
      if (Duration(z,"millis")<1.minute){
        Ok("cred are"+user+z)
      }
      else{
        Unauthorized("Credentials expired"+z)
      }})
      .getOrElse(Unauthorized("no token"))
  }
  def discard=Action{
    Ok("Bye").withNewSession
  }
}
