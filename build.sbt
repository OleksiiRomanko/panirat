name := "panibrat"

version := "1.0"

lazy val `panibrat` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

libraryDependencies ++= Seq(jdbc,cache, ws, specs2 % Test, "org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24")

libraryDependencies ++=Seq("org.apache.commons" % "commons-email" % "1.4")

libraryDependencies ++=Seq("com.typesafe.play" %% "play-mailer" % "3.0.1")

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies ++=Seq(  "org.webjars" % "bootstrap" % "2.3.2")

libraryDependencies ++= Seq( "org.webjars" %% "webjars-play" % "2.3.0-2")

libraryDependencies ++=  Seq( "org.webjars" % "angularjs" % "1.1.5-1")

//libraryDependencies ++= Seq("org.apache.logging.log4j" %% "log4j-api-scala" % "11.0")
//
//libraryDependencies ++= Seq("org.apache.logging.log4j" %% "log4j-api" % "2.8.2")
//
//libraryDependencies ++= Seq("org.apache.logging.log4j" %% "log4j-core" % "2.8.2" % "runtime")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"







PlayKeys.playDefaultPort := 8083