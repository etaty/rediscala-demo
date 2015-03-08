name:="rediscala-demo"

scalaVersion:="2.10.5"

resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"

libraryDependencies ++= Seq(
  "com.etaty.rediscala" %% "rediscala" % "1.4.2"
)
