
package controllers
import javax.inject.{Inject, Singleton}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import repo.{DB, SiteRepoImpl}

import scala.concurrent.Future

//object Site extends Controller {
//
//
//  def register=Action{
//    Ok("Here is registration page")
//  }
//  def create=TODO
//  def read(id:String)=TODO
//  def update(id:String)=TODO
//  def delete(id:String)=TODO
//}

class Site @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with MongoController with ReactiveMongoComponents {
  import controllers.WidgetFields._
  implicit class RichResult (result: Result) {
    def enableCors =  result.withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Cache-Control"->"public"
      , "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE, HEAD"   // OPTIONS for pre-flight
      , "Access-Control-Allow-Headers" -> "Accept, Cookie,Content-Type, Origin, X-Json, X-Prototype-Version, X-Requested-With" //, "X-My-NonStd-Option"
      , "Access-Control-Allow-Credentials" -> "true"
    )
  }
  def siteRepo = new SiteRepoImpl(reactiveMongoApi,DB.Page)
//  def index=Action.async { implicit request=>
//  Future(Ok(views.html.index("user")))
//  }
  def get =Action.async { implicit request =>
    siteRepo.find().map(users => Ok(Json.toJson(users)).enableCors)
  }


  def read(name: String) = Action.async { implicit request =>
    siteRepo.select(BSONDocument(Name -> name))
      .map(user => Ok(Json.toJson(user)))
  }

  def delete(id: String) = Action.async {
    siteRepo.remove(BSONDocument(Id -> BSONObjectID(id)))
    .map(result => Accepted)
  }

  def create = Action.async(BodyParsers.parse.json) { implicit request => {
    val name = (request.body\  Name).as[String]
    val description = (request.body\ Description).as[String]
    val author = (request.body\ Author).as[String]
    siteRepo.save(BSONDocument(
      Name ->name,
      Description ->description,
      Author ->author
    ) ).map(result => Created)
  }
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request => {
    val name = (request.body \ Name).as[String]
    val description = (request.body \ Description).as[String]
    val author = (request.body \ Author).as[String]
    siteRepo.update(BSONDocument(Id ->BSONObjectID(id)),
    BSONDocument("$set" ->BSONDocument(
      Name->  name,
      Description -> description,
      Author -> author) ) )
    .map(result => Accepted)
  }
  }
//  def indextry = Action {
//    Ok(views.html.index("ddd"))
//  }
}

object WidgetFields {
  val Id = "_id"
  val Name ="name"
  val Description = "description"
  val Author = "author"
}

import play.api.libs.json.Json
import play.api.mvc._

/**
  * Created by Oleksii on 16.04.2017.
  */

