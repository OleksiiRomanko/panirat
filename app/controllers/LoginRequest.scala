package controllers

/**
  * Created by Oleksii on 25.05.2017.
  */
case class LoginRequest(username: String, password: String) {

  // Simple username-password map in place of a database:
  val validUsers = Map("sysadmin" -> "password1", "root" -> "god")

  def authenticate = validUsers.exists(_ == (username, password))
}
