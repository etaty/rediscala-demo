name:="rediscala-demo"

scalaVersion:="2.10.2"

resolvers += "rediscala" at "https://raw.github.com/etaty/rediscala-mvn/master/releases/"

libraryDependencies ++= Seq(
  "com.etaty.rediscala" %% "rediscala" % "1.3"
)
