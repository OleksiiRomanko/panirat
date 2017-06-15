package controllers
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Level
import javax.inject.Inject

import play.api.Configuration
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, BodyParsers, Controller, Result}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import repo.{DB, SiteRepoImpl}
import javax.inject.{Inject, Singleton}

import misc.{Encryptor, ImageSaver}
import models.SearchFilesObj
import play.api.Configuration
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
  * Created by Oleksii on 25.05.2017.
  */
class ProfileController@Inject()  (val playconfiguration: Configuration,val reactiveMongoApi: ReactiveMongoApi,mailer: MailerClient) extends Controller
  with MongoController with ReactiveMongoComponents {

  import controllers.UserFields._
  def offerRepo = new SiteRepoImpl(reactiveMongoApi, DB.Offers)

  def portfolioRepo=new SiteRepoImpl(reactiveMongoApi, DB.Portfolio)

  def userRepo = new SiteRepoImpl(reactiveMongoApi, DB.Users)

  def unconfirmedRepo = new SiteRepoImpl(reactiveMongoApi, DB.Uncorfirmed)

  def feedbacksRepo= new SiteRepoImpl(reactiveMongoApi, DB.Feedbacks)

  def individualRepo= new SiteRepoImpl(reactiveMongoApi, DB.Individual)
  def wishesRepo= new SiteRepoImpl(reactiveMongoApi, DB.Wishes)

  implicit class RichResult(result: Result) {
    def enableCors = result.withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Cache-Control"->"public"
      , "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE, HEAD" // OPTIONS for pre-flight
      , "Access-Control-Allow-Headers" -> "Access-Control-Allow-Origin,Accept,Cookie, Content-Type, Origin, X-Json, X-Prototype-Version, X-Requested-With" //, "X-My-NonStd-Option"
        , "Access-Control-Allow-Credentials" -> "true"
    )
  }

  def saveindividual=Action.async(BodyParsers.parse.json){implicit request=>
    //     val user=request.session.get("user").getOrElse("-1")
    ////      var lst=List[Map(String,String)]
    ////      var mp=Map(String,String)
    //      println(user)
    //      val address="public/assets/pictures/"+"bogdan549@gmail.com"
    //      val files=SearchFilesObj.all(address).map(a=>
    //        Map("url"->("/assets/pictures/"+"bogdan549@gmail.com"+"/"+a))+
    //          ("description"->"sex escort")+
    //          ("header"->"name of ware"))
    //
    //      Ok(Json.toJson(files)).enableCors
    //    Redirect("/").enableCors
//    val Surname="individual_surname"
//    val Firstname="individual_first_name"
//    val Middlename="individual_middle_name"
//    val Profession="ID_individual_profession"
//    val Birthdate="individual_birth_date"
//    val Sex="ID_individual_sex"
//    val Educationplace="ID_individual_education_place"
//    val Graduationyear="ID_individual_graduation_year"
    val mail = request.session.get("user").getOrElse("-1")
    val surname = (request.body \ OfferFields.Surname).as[String]
    val firstname=(request.body \ OfferFields.Firstname).as[String]
    val middlename = (request.body \ OfferFields.Middlename).as[String]
    val profession = (request.body \ OfferFields.Profession).as[String]
    val birthdate=(request.body \ OfferFields.Birthdate).as[String]
    val sex = (request.body \ OfferFields.Sex).as[String]
    val educationplace=(request.body \ OfferFields.Educationplace).as[String]
    val graduationyear = (request.body \ OfferFields.Graduationyear).as[String]
    val profileimage=(request.body \ OfferFields.Userphoto).as[String]
    val city=(request.body \ OfferFields.Usercity).as[String]
//    val svr=new ImageSaver()
//    val url=svr.saveImage(image,mail)
    val svr=new ImageSaver()
    val url=svr.saveImage(profileimage,mail)
    println(mail)
    println(graduationyear)
    println(url)
    individualRepo.save(BSONDocument(
      OfferFields.Mail -> mail ,
      OfferFields.Surname -> surname ,
      OfferFields.Firstname -> firstname,
      OfferFields.Middlename -> middlename,
      OfferFields.Profession -> profession,
      OfferFields.Birthdate -> birthdate,
      OfferFields.Sex -> sex,
      OfferFields.Educationplace -> educationplace,
      OfferFields.Graduationyear -> graduationyear,
      OfferFields.Userphoto->url,
      OfferFields.Usercity->city
    )).map(a => Ok("Ok").enableCors)
  }

  def loadindividual=Action.async{ implicit request =>
    val mail = request.session.get("user").getOrElse("-1")
    println(mail)
    val a = individualRepo.query(BSONDocument(OfferFields.Mail -> mail))
    val futurePersons: Future[List[JsObject]] = a.toList()
    futurePersons.map { persons =>
      Ok{"sdf"}
//      a=persons match {
//        case h :: Nil => true}
//      if(true){Ok(Json.toJson(persons)).enableCors}
    }
  }

  def savefeedbacks=Action.async(BodyParsers.parse.json){implicit request=>
    //     val user=request.session.get("user").getOrElse("-1")
    ////      var lst=List[Map(String,String)]
    ////      var mp=Map(String,String)
    //      println(user)
    //      val address="public/assets/pictures/"+"bogdan549@gmail.com"
    //      val files=SearchFilesObj.all(address).map(a=>
    //        Map("url"->("/assets/pictures/"+"bogdan549@gmail.com"+"/"+a))+
    //          ("description"->"sex escort")+
    //          ("header"->"name of ware"))
    //
    //      Ok(Json.toJson(files)).enableCors
    //    Redirect("/").enableCors
    val mail = request.session.get("user").getOrElse("-1")
    val receiver = (request.body \ OfferFields.Receiver).as[String]
    val sender=(request.body \ OfferFields.Sender).as[String]
    val waretask = (request.body \ OfferFields.WareTask).as[String]

    println(waretask)
    val stars=(request.body \ OfferFields.Stars).as[String]
    val text = (request.body \ OfferFields.Text).as[String]
    val date=(request.body \ OfferFields.Date).as[String]
    println("trying profilecontroller")
    println("mail")
    println(mail)
    println(stars)


    portfolioRepo.save(BSONDocument(
      OfferFields.Receiver-> receiver ,
      OfferFields.Sender -> sender,
      OfferFields.WareTask -> waretask,
      OfferFields.Stars-> stars ,
      OfferFields.Text -> text,
      OfferFields.Date -> date

    )).map(a => Ok("Ok").enableCors)
  }
  def loadfeedbacks=Action.async { implicit request =>
    val mail = request.session.get("user").getOrElse("-1")
    val a = portfolioRepo.query(BSONDocument(OfferFields.Receiver -> mail))
    val futurePersons: Future[List[JsObject]] = a.toList()
    futurePersons.map { persons =>
      Ok(Json.toJson(persons)).enableCors
    }
  }

  def saveportfolio=Action.async(BodyParsers.parse.json){implicit request=>
//     val user=request.session.get("user").getOrElse("-1")
////      var lst=List[Map(String,String)]
////      var mp=Map(String,String)
//      println(user)
//      val address="public/assets/pictures/"+"bogdan549@gmail.com"
//      val files=SearchFilesObj.all(address).map(a=>
//        Map("url"->("/assets/pictures/"+"bogdan549@gmail.com"+"/"+a))+
//          ("description"->"sex escort")+
//          ("header"->"name of ware"))
//
//      Ok(Json.toJson(files)).enableCors
//    Redirect("/").enableCors
    val mail = request.session.get("user").getOrElse("-1")
    val description = (request.body \ OfferFields.Desription).as[String]
    val image=(request.body \ OfferFields.Image).as[String]
    println("tying profilecontroller")
    println("mail")
    println(mail)
    println("image")
    println(image)

    println("description")
    println(description)
    val svr=new ImageSaver()
    val url=svr.saveImage(image,mail)
    portfolioRepo.save(BSONDocument(
      OfferFields.Mail-> mail,
      OfferFields.Desription -> description,
      OfferFields.Ur -> url
    )).map(a => Ok("Ok").enableCors)
  }
  def loadportfolio=Action.async { implicit request =>
    val mail = request.session.get("user").getOrElse("-1")
    val a = portfolioRepo.query(BSONDocument(OfferFields.Mail -> mail))
    val futurePersons: Future[List[JsObject]] = a.toList()
    futurePersons.map { persons =>
      Ok(Json.toJson(persons)).enableCors
    }
  }
  def loadpic=Action.async { implicit request =>
    val mail = request.session.get("user").getOrElse("-1")
    var lst=List()
    val a=offerRepo.query(BSONDocument(OfferFields.Mail->mail))


    // gather all the JsObjects in a list
    val futurePersons: Future[List[JsObject]] = a.toList()
    //  val futurePersonsJsonArray: Future[JsArray] = a.collect[List[JsObject]] ( persons => Json.arr(persons) )

    // everything's ok! Let's reply with the array
    //  futurePersonsJsonArray.map { persons =>
    //    Ok(persons)
    //  }
    //  val futurePersonsList: Future[JsArray] = futurePersons.map[JsArray](z=>{
    //    println("aaa")
    //    var z=List()
    //    for(a<-z){
    //      z=z::()a
    //    }
    //    Json.arr(z)})

    // transform the list into a JsArray
    //  val futurePersonsJsonArray: Future[JsArray] =
    //    futurePersonsList.map { persons => Json.arr(persons) }

    // everything's ok! Let's reply with the array
    futurePersons.map { persons =>
      Ok(Json.toJson(persons)).enableCors
    }
  }
  def loadoffercat = Action.async{implicit request =>
    val lst=List("Подарунки","Бізнес і послуги","Електроніка","Мода і стиль","Відпочинок","Спорт")
    Future(Ok(Json.toJson(lst)).enableCors)
  }

  def loadoffersubcat = Action.async{implicit request =>
    val map=Map("Подарунки"->"Іграшки","Бізнес і послуги"->"Юридичні послуги","Електроніка"->"Телефони","Мода і стиль"->"Взуття","Відпочинок"->"Мастеркласи","Спорт"->"Бойові мистецтва")
    var lst=List(Map("cat"->"Бізнес і послуги","sub"->"Юридичні послуги"))
    val a=List(Map("cat"->"Бізнес і послуги","sub"->"Юридичні послуги"))
    val b=Map("cat"->"Подарунки","sub"->"Іграшки")
    lst=Map("cat"->"Спорт","sub"->"Бойові мистецтва")::Map("cat"->"Мода і стиль","sub"->"Взуття")::Map("cat"->"Відпочинок","sub"->"Майстеркласи")::Map("cat"->"Електроніка","sub"->"Телефони")::b::lst
    print("ok")
    Future(Ok(Json.toJson(lst)).enableCors)
  }

  def savewish=Action.async(BodyParsers.parse.json){
    implicit request =>
      val description = (request.body \ OfferFields.Id).as[String]
      val a=Json.obj( "$oid"-> description )
      val query = Json.obj("_id" -> a)
      val mailwisher="sd"
      offerRepo.selectjson(query).map(wish => {
      try{
        val u=wish.get
        val mailowner=wish.get.value.get(OfferFields.Mail).getOrElse("-1").toString
        wishesRepo.save(BSONDocument(
          OfferFields.Owner->mailowner,
          OfferFields.Wisher->mailwisher,
          OfferFields.Id->description
        ))
      //If credentials are valid -> rewriting user to main database and updating field in unregistered database
//      if (i==user.get.value.get(Secret).getOrElse("-1").toString) {
//        unconfirmedRepo.update(BSONDocument(Mail->email),BSONDocument(Mail->email,Validated->Yes))
//        userRepo.save(BSONDocument(Mail->email,
//          Password->u.value.get(Password).getOrElse("-1").toString,
//          BusinessType->u.value.get(BusinessType).getOrElse("-1").toString))
//        Ok("true")}
//      else{
//        if(user.get.value.get(Secret).getOrElse("-1").toString=="Yes"){Ok("User already confirmed")}
//        else{Ok("No such user or secret is incorrect")}
//      }
      }
      catch{case _:Throwable =>Ok("some error")}

    })
      Future(Ok("sdf"))
  }

  def loadofferlist=Action.async(BodyParsers.parse.json){
    implicit request =>
      val description = (request.body \ OfferFields.Keys).as[String]

      println(description)
      val b=Json.obj("$regex" ->  (".*" + description + ".*"))
      val query = Json.obj("$regex" ->  (".*" + description + ".*"))
      offerRepo.queryjson(query).collect[List]() map {
        case objects => Ok(Json.toJson(objects)).enableCors
      }
  }
  def savepic = Action.async(BodyParsers.parse.json) { implicit request =>
    val mail = request.session.get("user").getOrElse("-1")
    val header = (request.body \ OfferFields.Hd).as[String]
    val description = (request.body \ OfferFields.Desription).as[String]
    val image=(request.body \ OfferFields.Image).as[String]
    val typ=(request.body \ OfferFields.Type).as[String]
    val subtype=(request.body \ OfferFields.Subtype).as[String]
    val dat=(request.body \ OfferFields.Data).as[String]
    println("mail")
    println(mail)
    println("image")
    println(image)
    println("header")
    println(header)
    println("description")
    println(description)
    offerRepo.save(BSONDocument(
      OfferFields.Mail-> mail,
      OfferFields.Hd -> header,
      OfferFields.Desription -> description,
      OfferFields.Image -> image,
      OfferFields.Data->dat,
      OfferFields.Type->typ,
      OfferFields.Subtype->subtype
    )).map(a => Ok("Ok").enableCors)
  }






}
object OfferFields{
  val Image="image"
  val Mail="email"
  val Hd="header"
  val Ur="url"
  val Desription="description"
  val Sender="sender"
  val Receiver="receiver"
  val WareTask="waretask"
  val Stars="stars"
  val Text="response"
  val Date="date"
  val Yellow="yellow"
  val Gray="gray"
  val Surname="individual_surname"
  val Firstname="individual_first_name"
  val Middlename="individual_middle_name"
  val Profession="ID_individual_profession"
  val Birthdate="individual_birth_date"
  val Sex="ID_individual_sex"
  val Educationplace="ID_individual_education_place"
  val Graduationyear="ID_individual_graduation_year"
  val Userphoto="user_photo"
  val Usercity="user_city"
  val Keys="keys"
  val Id="id"
  val Adddate="addDate"
  val Owner="owner"
  val Wisher="wisher"
  val Type="type"
  val Subtype="subType"
  val Data="date"
//  val
}
//"individual_surname": "",
//"individual_first_name": "",
//"individual_middle_name": "",
//"ID_individual_profession": "",
//"individual_birth_date": "",
//"ID_individual_sex": "",
//"ID_individual_education_place": "",
//"ID_individual_graduation_year": "",
//"user_phone_number": ""