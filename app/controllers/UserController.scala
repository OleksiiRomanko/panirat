package controllers

import javax.inject.{Inject, Singleton}

import misc.Encryptor
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
  * Created by Oleksii on 22.04.2017.
  */

class UserController@Inject()  (val playconfiguration: Configuration,val reactiveMongoApi: ReactiveMongoApi,mailer: MailerClient) extends Controller
  with MongoController with ReactiveMongoComponents {

  import controllers.UserFields._
  def userRepo = new SiteRepoImpl(reactiveMongoApi,DB.Users)
  def unconfirmedRepo=new SiteRepoImpl(reactiveMongoApi,DB.Uncorfirmed)
  implicit class RichResult (result: Result) {
    def enableCors =  result.withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Cache-Control"->"public",
      "Set-Cookie"->"name=value",
      "Access-Control-Allow-Credentials"-> "true"
      , "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE, HEAD"   // OPTIONS for pre-flight
      , "Access-Control-Allow-Headers" -> "Access-Control-Allow-Origin,Accept,Cookie, Content-Type, Origin, X-Json, X-Prototype-Version, X-Requested-With" //, "X-My-NonStd-Option"
      , "Access-Control-Allow-Credentials" -> "true"
    )
  }

  def send(mail:String,ref:String) = {
    val cid = "1234"
    val email = Email(
      "Panibrat.com sign-in confirmation",
      "Panibrat <peekaboochat@gmail.com>",
      Seq("Miss TO <"+mail+">"),
      bodyText = Some("A text message"),
      bodyHtml = Some("Вітаємо, панібрате!" +"\n"+body3+"?email="+mail+"&secret="+ref+body4)
    )
    mailer.send(email)
  }

  /**
    * Function for validating user
    *
    * @return Ok("true") - if valid code
    *         Ok("No such user or secret is incorrect") - if invalid code
    *         Ok("User already confirmed") - if user was already confirmed
    */
  def validate()=Action.async{implicit request=>{
    val email=request.getQueryString(Mail).getOrElse("-1")
    val secret=request.getQueryString(Secret).getOrElse("-1")

    unconfirmedRepo.select(BSONDocument(Mail->email))
         .map(user => {
           try{val u=user.get
             val i="\""+secret+"\""
             //If credentials are valid -> rewriting user to main database and updating field in unregistered database
             if (i==user.get.value.get(Secret).getOrElse("-1").toString) {
               unconfirmedRepo.update(BSONDocument(Mail->email),BSONDocument(Mail->email,Validated->Yes))
               userRepo.save(BSONDocument(Mail->email,
                 Password->u.value.get(Password).getOrElse("-1").toString,
                 BusinessType->u.value.get(BusinessType).getOrElse("-1").toString))
               Ok("true")}
               else{
                 if(user.get.value.get(Secret).getOrElse("-1").toString=="Yes"){Ok("User already confirmed")}
                 else{Ok("No such user or secret is incorrect")}
             }
           }
           catch{case _:Throwable =>Ok("some error")}

         })
   }}
//TODO:
  //get rid of unnecesarry parameter
  /**Function for responding to OPTIONS REQUEST
    *
    * @param path - unnecesary parameter
    * @return
    */
  def info(path:String)=Action{implicit request=>{
  Ok("ok").enableCors
}}


  /**
    * Testing Sanias fields
    *
    * @return
    */
  def test=Action.async(BodyParsers.parse.tolerantFormUrlEncoded) { implicit request => {
    println(request.body)
    println(request.body.get("individual_surname").get)
    request.body.keySet.foreach(println)
  Future(Ok("ok"))
  }
  }

  /**
    * Creating database user
    *
    * @return
    */
  def auth=Action.async(BodyParsers.parse.json){implicit request=>{
    try{
      val mail = (request.body \ Mail).as[String]
      val password = (request.body \ Password).as[String]
      println(password)
      userRepo.select(BSONDocument(Mail->mail)).map(user=>{
        val pswd=user.get.value.get(Password).getOrElse(-1).toString
        val ttt=Encryptor.decrypt(playconfiguration.getString("application.secret").get,pswd)
        if (ttt==password){Ok("/").enableCors}
//          .withSession("user"->mail,"time"->System.currentTimeMillis().toString).enableCors}
        else Ok("Fault email or password").enableCors
      })
    }
  }}




  def create = Action.async(BodyParsers.parse.json) { implicit request => {
    println(request.body.toString())
    try {
      val mail = (request.body \ Mail).as[String]
      val password = (request.body \ Password).as[String]
//      val business=(request.body \ BusinessType).as[String]

      val r = scala.util.Random
      val secret=1000+r.nextInt(999)
      send(mail, secret.toString)
      unconfirmedRepo.save(BSONDocument(
        Mail -> mail,
        Password -> Encryptor.encrypt(playconfiguration.getString("application.secret").get,password),
//        BusinessType->business,
        Secret->secret.toString,
        Validated->No
      )).map(result => Created)
    }
    catch{
      case e:Throwable=>{
        println(e.printStackTrace())
        Future(Conflict("Not All fields"))}
    }





  }
  }
  var body3:String="<html>\n  <head>\n  </head>\n  <body>\n   <a href= http://193.33.64.97:8083/users/validate/"
  var body4:String=">Натисніть на посилання для завершення реєстрації</a>\n  </body>\n</html>"
  var body0: String = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" + "    <title>Demystifying Email Design</title>\n" + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" + "</head>\n" + "<body style=\"margin: 0; padding: 0;\">\n" + "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse;font-family: Gadugi, sans-serif;\">\n" + "    <tr>\n" + "        <td align=\"center\" bgcolor=\"#f1f1f1\" style=\"padding: 40px 0 30px 0;\">\n" + "\n" + "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" + "                <tr>\n" + "                    <td align=\"center\" style=\"padding: 0 0 10px 0px\">\n" + "                        <img src=\"https://www.peekaboochat.com/icons/pb.png\" alt=\"PeekabooLogo\" width=\"40\" height=\"40\" style=\"display:block\"/>\n" + "                    </td>\n" + "                </tr>\n" + "                <tr>\n" + "                    <td style=\"padding:30px 100px 20px 100px; font-size: 28px; text-align: center;\">\n" + "                        Welcome to Peekaboo\n" + "                    </td>\n" + "                </tr>\n" + "                <tr>\n" + "                    <td>\n" + "                        <table border=\"0\" align=\"center\" bgcolor=\"#ffffff\" cellpadding=\"0\" cellspacing=\"0\" width=\"400\"\n" + "                                style=\"border-radius: 6px\">\n" + "                            <tr>\n" + "                                <td style=\"padding:20px 20px 0 20px;\" >\n" + "                                    Please, confirm your registration with this code:\n" + "                                </td>\n" + "                            </tr>\n" + "                            <tr>\n" + "                                <td align=\"center\" style=\"padding: 10px 20px 10px 20px; color: #00c9a8; font-size: 24px\">"
  var body1: String = "</td>\n" + "                            </tr>\n" + "                            <tr>\n" + "                                <td style=\"padding: 0 20px 20px 20px\">\n" + "                                    Thank you!\n" + "                                </td>\n" + "                            </tr>\n" + "                        </table>\n" + "                    </td>\n" + "                </tr>\n" + "            </table>\n" + "        </td>\n" + "    </tr>\n" + "    <tr>\n" + "        <td bgcolor=\"#303030\" style=\"color: #cacaca\">\n" + "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" + "                <tr>\n" + "                    <td align=\"center\" style=\"padding: 30px 0 0 0\">\n" + "                        <img src=\"https://www.peekaboochat.com/icons/peekaboo.png\" width=\"154\" height=\"32\" alt=\"Peekaboo\"/>\n" + "                    </td>\n" + "                </tr>\n" + "                <tr>\n" + "                    <td align=\"center\" style=\"padding: 30px 0 25px 0px\">\n" + "                        <img src=\"https://www.peekaboochat.com/icons/facebook.png\" alt=\"Facebook\" width=\"50\" height=\"50\" style=\"display:inline-block; padding: 0 15px 0 15px\"/>\n" + "                        <img src=\"https://www.peekaboochat.com/icons/twitter.png\" alt=\"Twitter\" width=\"50\" height=\"50\" style=\"display:inline-block; padding: 0 15px 0 15px\"/>\n" + "                        <img src=\"https://www.peekaboochat.com/icons/in.png\" alt=\"LinkedIn\" width=\"50\" height=\"50\" style=\"display:inline-block; padding: 0 15px 0 15px\"/>\n" + "                        <img src=\"https://www.peekaboochat.com/icons/insta.png\" alt=\"Instagram\" width=\"50\" height=\"50\" style=\"display:inline-block;padding: 0 15px 0 15px\"/>\n" + "                    </td>\n" + "                </tr>\n" + "                <tr>\n" + "                    <td align=\"center\">\n" + "                        &copy;2016 Canyon Capital LLC. All rights reserved.\n" + "                    </td>\n" + "                </tr>\n" + "                <tr>\n" + "                    <td align=\"center\" style=\"padding:0 0 30px 0;\">\n" + "                        640 Don Nicolas Road, Taos, New Mexico 87571, USA\n" + "                    </td>\n" + "                </tr>\n" + "            </table>\n" + "        </td>\n" + "    </tr>\n" + "\n" + "</table>\n" + "</body>\n" + "</html>\n"
}

object UserFields{
  val Firstname="firstname"
  val Lastname="lastname"
  val Username="nickname"
  val Password="password"
  val Mail="email"
  val BusinessType="businessType"
  val Secret="secret"
  val Validated="validated"
  val Yes="yes"
  val No="no"
}
