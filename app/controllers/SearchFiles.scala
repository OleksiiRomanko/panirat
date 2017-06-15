package controllers

import models.SearchFilesObj
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import scala.collection.mutable.ListBuffer
import java.io.File

object SearchFiles extends Controller {

  // Simple action - return search results as Json
  def perform(where:String,term:String) = Action {
    val m = SearchFilesObj.find(where,term)
    Ok(Json.toJson(m))
  }
}