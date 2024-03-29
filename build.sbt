name := "clickcounter-spray-scala"

version := "0.2"

scalaVersion := "2.11.12"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= {
  val akkaV      = "2.3.9"
  val sprayV     = "1.3.4"
  val sprayJsonV = "1.3.6"
  val specs2V    = "2.3.13"
  Seq(
    "io.spray"                %% "spray-can"            % sprayV,
    "io.spray"                %% "spray-routing" 	% sprayV,
    "io.spray"                %% "spray-json"    	% sprayJsonV,
    "io.spray"                %% "spray-testkit" 	% sprayV      % Test,
    "com.typesafe.akka"       %% "akka-actor"    	% akkaV,
    "org.specs2"              %% "specs2-core"   	% specs2V     % Test,
    "org.specs2"              %% "specs2-matcher-extra" % specs2V     % Test,
    "org.slf4j"               %  "slf4j-simple"  	% "1.7.36",
    "com.github.scredis"      %% "scredis"       	% "2.4.3",
    "net.databinder.dispatch" %% "dispatch-core" 	% "0.13.4"    % Test
  )
}

// IntelliJ Scala plugin reports false positive errors here

Revolver.settings

// enablePlugins(JavaAppPackaging)

coverageExcludedPackages := """.*\.Boot;.*\.ClickcounterServiceActor"""

test in assembly := {}
