package controllers

/**
  * Created by Oleksii on 30.04.2017.
  */
import javax.inject.{Inject, Singleton}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import play.mvc.Http.Response
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID, Producer}
import repo.{DB, SiteRepoImpl}
import play.api.libs.mailer.{Email, MailerClient}
import reactivemongo.bson.BSONValue.ExtendedBSONValue

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by Oleksii on 22.04.2017.
  */

class AdminController@Inject()(val reactiveMongoApi: ReactiveMongoApi,mailer: MailerClient) extends Controller
  with MongoController with ReactiveMongoComponents {
  def userRepo = new SiteRepoImpl(reactiveMongoApi,DB.Users)
  def unconfirmedRepo=new SiteRepoImpl(reactiveMongoApi,DB.Uncorfirmed)
  def getusersunvalid=Action.async { implicit request =>{
    unconfirmedRepo.find().map(users => Ok(Json.toJson(users)))
  }}
  def getusersvalid=Action.async { implicit request =>{
    userRepo.find().map(users => Ok(Json.toJson(users)))
  }}
}