name := "apikeeper"

scalaVersion := "2.12.10"

addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full))

scalacOptions := Seq(
  "-encoding",
  "utf8",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps",
  "-Ypartial-unification"
)

val catsVersion = "2.0.0"
val fs2Version = "2.1.0"
val circeVersion = "0.12.3"
val http4sVersion = "0.21.0-RC4"
val tapirVersion = "0.12.20"
val logbackVersion = "1.2.3"
val logger4CatsVersion = "0.3.1"
val pureConfigVersion = "0.12.1"
val enumeratumVersion = "1.5.15"
val distageVersion = "0.10.1"
val neo4jDriverVersion = "4.0.0"
val scalaTestVersion = "3.1.0"
val testContainersVersion = "0.35.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsVersion,
  "co.fs2" %% "fs2-core" % fs2Version,
  "co.fs2" %% "fs2-io" % fs2Version,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe" % tapirVersion,
  "org.neo4j.driver" % "neo4j-java-driver" % neo4jDriverVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.7mind.izumi" %% "distage-core" % distageVersion,
  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "com.beachape" %% "enumeratum-circe" % enumeratumVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.pure4s" %% "logger4s-cats" % logger4CatsVersion,
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "com.dimafeng" %% "testcontainers-scala" % testContainersVersion % Test,
  "com.dimafeng" %% "testcontainers-scala-neo4j" % testContainersVersion % Test
)

mainClass in assembly := Some("apikeeper.ApiKeeper")
assemblyJarName in assembly := "apikeeper.jar"
test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", xs @ _*)       => MergeStrategy.deduplicate
  case "application.conf"                  => MergeStrategy.concat
  case _                                   => MergeStrategy.first
}
