//package controllers
//
//import javax.inject.{Inject, Singleton}
//
//import misc.Encryptor
//import play.api.Configuration
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
//import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
//import play.api.mvc._
//import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
//import play.mvc.Http.Response
//import reactivemongo.api.commands.WriteResult
//import reactivemongo.bson.{BSONDocument, BSONObjectID, Producer}
//import repo.{DB, SiteRepoImpl}
//import play.api.libs.mailer.{Email, MailerClient}
//import reactivemongo.bson.BSONValue.ExtendedBSONValue
//
//import scala.concurrent.{Await, Future}
//import scala.util.Try
//
///**
//  * Created by Oleksii on 22.04.2017.
//  */
//
//class OfferController@Inject()  (val playconfiguration: Configuration,val reactiveMongoApi: ReactiveMongoApi,mailer: MailerClient) extends Controller
//  with MongoController with ReactiveMongoComponents {
//  def offerRepo = new SiteRepoImpl(reactiveMongoApi, DB.Offers)
//
//  implicit class RichResult(result: Result) {
//    def enableCors = result.withHeaders(
//      "Access-Control-Allow-Origin" -> "*"
//      , "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE, HEAD" // OPTIONS for pre-flight
//      , "Access-Control-Allow-Headers" -> "Accept,Cookie, Content-Type, Origin, X-Json, X-Prototype-Version, X-Requested-With" //, "X-My-NonStd-Option"
//      , "Access-Control-Allow-Credentials" -> "true"
//    )
//  }
//def loadpic=Action.async { implicit request =>
//  val mail = request.session.get("user").getOrElse("-1")
//  var lst=List()
//  val a=offerRepo.query(BSONDocument(OfferFields.Mail->mail))
//
//
//  // gather all the JsObjects in a list
//  val futurePersons: Future[List[JsObject]] = a.toList()
////  val futurePersonsJsonArray: Future[JsArray] = a.collect[List[JsObject]] ( persons => Json.arr(persons) )
//
//  // everything's ok! Let's reply with the array
////  futurePersonsJsonArray.map { persons =>
////    Ok(persons)
////  }
////  val futurePersonsList: Future[JsArray] = futurePersons.map[JsArray](z=>{
////    println("aaa")
////    var z=List()
////    for(a<-z){
////      z=z::()a
////    }
////    Json.arr(z)})
//
//  // transform the list into a JsArray
////  val futurePersonsJsonArray: Future[JsArray] =
////    futurePersonsList.map { persons => Json.arr(persons) }
//
//  // everything's ok! Let's reply with the array
//  futurePersons.map { persons =>
//    Ok(Json.toJson(persons))
//  }
//    }
//
//  def savepic = Action.async(BodyParsers.parse.json) { implicit request =>
//    val mail = request.session.get("user").getOrElse("-1")
//    val header = (request.body \ OfferFields.Hd).as[String]
//    val description = (request.body \ OfferFields.Desription).as[String]
//    val offerType = (request.body \ OfferFields.OfferType).as[String]
//    val image=(request.body \ OfferFields.Image).as[String]
//    println("mail")
//    println(mail)
//    println("image")
//    println(image)
//    println("header")
//    println(header)
//    println("description")
//    println(description)
//    println("offer")
//    println(offerType)
//    offerRepo.save(BSONDocument(
//      OfferFields.Mail-> mail,
//      OfferFields.Hd -> header,
//      OfferFields.Desription -> description,
//      OfferFields.OfferType -> offerType,
//      OfferFields.Image -> image
//    )).map(a => Ok("Ok"))
//  }
//
//  def testpic = Action(parse.multipartFormData) { request =>
//    request.body.file("picture").map { picture =>
//      import java.io.File
//      val filename = picture.filename
//      val contentType = picture.contentType
//      picture.ref.moveTo(new File(s"/tmp/picture/$filename"))
//      Ok("File uploaded")
//    }.getOrElse {
//      Ok("error")
//    }
//  }
//}
//
//object OfferFields{
//  val Image="image"
//  val Mail="email"
//  val Hd="header"
//  val Ur="url"
//  val Desription="description"
//  val OfferType="offer_type"
//}