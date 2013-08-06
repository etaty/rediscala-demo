import akka.util.ByteString
import redis.RedisClient
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ExampleTransaction extends App {
  implicit val akkaSystem = akka.actor.ActorSystem()

  val redis = RedisClient()

  val redisTransaction = redis.transaction()
  redisTransaction.exec()
  redisTransaction.watch("a")
  val set = redisTransaction.set("a", "abc")
  val decr = redisTransaction.decr("a")
  val get = redisTransaction.get("a")
  redisTransaction.exec()
  val r = for {
    s <- set
    g <- get
  } yield {
    assert(s)
    println("ok : set(\"a\", \"abc\")")
    assert(g == Some(ByteString("abc")))
    println("ok : get(\"a\") == \"abc\"")
  }
  decr.onFailure({
    case error => println(s"decr failed : $error")
  })
  Await.result(r, 10 seconds)
  akkaSystem.shutdown()
}
