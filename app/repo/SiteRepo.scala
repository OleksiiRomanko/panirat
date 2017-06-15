package repo

/**
  * Created by Oleksii on 16.04.2017.
  */
import javax.inject.Inject

import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.{ExecutionContext, Future}

trait SiteRepo {
  def find()(implicit ec: ExecutionContext): Future[List[JsObject]]

  def select(selector: BSONDocument)(implicit ec: ExecutionContext): Future[Option[JsObject]]

  def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult]

  def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult]

  def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult]
}

class SiteRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi,coll:String) extends SiteRepo {

  def collection = reactiveMongoApi.db.collection[JSONCollection](coll);

  override def find()(implicit ec: ExecutionContext): Future[List[JsObject]] = {
    val genericQueryBuilder = collection.find(Json.obj());
    val cursor = genericQueryBuilder.cursor[JsObject](ReadPreference.Primary);
    cursor.collect[List]()
  }

  override def select(selector: BSONDocument)(implicit ec: ExecutionContext): Future[Option[JsObject]] = {
    collection.find(selector).one[JsObject]
  }

  def selectjson(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[JsObject]] = {
    collection.find(selector).one[JsObject]
  }

  def query (selector: BSONDocument)(implicit ec: ExecutionContext)= {
    collection.find(selector).cursor[JsObject]
  }

  def queryjson (selector: JsObject)(implicit ec: ExecutionContext)= {
    collection.find(selector).cursor[JsObject]
  }
  override def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(selector, update)
  }

  override def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.remove(document)
  }
def drop(): Future[Unit]={
  import scala.concurrent.ExecutionContext.Implicits.global
  collection.drop()}
  override def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(BSONDocument("_id" -> document.get("_id").getOrElse(BSONObjectID.generate)), document, upsert = true)
  }

}
object DB{
  val Users="users"
  val Page="pages"
  val Offers="offers"
  val Uncorfirmed="unconfirmed"
  val Portfolio="portfolio"
  val Feedbacks="feedbacks"
  val Individual="individual"
  val Wishes="wishes"
}

