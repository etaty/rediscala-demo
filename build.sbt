name:="rediscala-demo"

scalaVersion:="2.10.2"

resolvers += "rediscala" at "https://github.com/etaty/rediscala-mvn/raw/master/releases/"

libraryDependencies ++= Seq(
  "com.etaty.rediscala" %% "rediscala" % "1.0"
)