name := "apikeeper"

scalaVersion := "2.12.10"

enablePlugins(JavaAppPackaging)

scalacOptions := Seq(
  "-encoding",
  "utf8",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

val catsVersion = "2.0.0"
val fs2Version = "2.1.0"
val circeVersion = "0.12.3"
val http4sVersion = "0.21.0-RC1"
val tapirVersion = "0.11.9"
val logbackVersion = "1.2.3"
val logger4CatsVersion = "0.3.1"
val pureConfigVersion = "0.12.1"
val enumeratumVersion = "1.5.15"
val neo4jDriverVersion = "4.0.0"
val scalaTestVersion = "3.1.0"
val testContainersVersion = "0.35.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsVersion,
  "co.fs2" %% "fs2-core" % fs2Version,
  "co.fs2" %% "fs2-io" % fs2Version,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "com.softwaremill.tapir" %% "tapir-core" % tapirVersion,
  "com.softwaremill.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.tapir" %% "tapir-swagger-ui-http4s" % tapirVersion,
  "org.neo4j.driver" % "neo4j-java-driver" % neo4jDriverVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.pure4s" %% "logger4s-cats" % logger4CatsVersion,
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "com.dimafeng" %% "testcontainers-scala" % testContainersVersion % Test
)

mainClass in Compile := Some("")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _                             => MergeStrategy.first
}
