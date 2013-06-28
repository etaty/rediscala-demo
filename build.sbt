name:="rediscala-demo"

scalaVersion:="2.10.2"

resolvers += "rediscala" at "https://github.com/etaty/rediscala/raw/master/dist/snapshots/"

libraryDependencies ++= Seq(
  "rediscala" %% "rediscala" % "0.1-SNAPSHOT"
)