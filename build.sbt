name:="rediscala-demo"

scalaVersion:="2.10.2"

resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"

libraryDependencies ++= Seq(
  "com.etaty.rediscala" %% "rediscala" % "1.3.1"
)
