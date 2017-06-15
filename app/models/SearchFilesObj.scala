package models

import java.io.File

import play.api.libs.json.Json

case class SearchFilesObj(text: String)

// Finds files in the current dir. matching the given search term
object SearchFilesObj {

  // Simple list of files in the current directory
  def all(where:String) = new File(where).listFiles().map(file => file.getName())

  // Simple case-sensitive filter
//  def find(term: String) = Result.all.filter(_.contains(term))
  def find(where:String,term: String) = all(where).filter(_.contains(term))
def main(args: Array[String]):Unit={
  val files=SearchFilesObj.all("public/assets/pictures/bogdan549@gmail.com").map(a=>Map("url"->("/assets/pictures/"+"bogdan549@gmail.com"+"/"+a))+("description"->"dscr"))
  for (a<-files){
    println(a)
  }
}
}

