import redis.{RedisBlockingClient, RedisClient}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ExampleRediscalaBlocking extends App {
  implicit val akkaSystem = akka.actor.ActorSystem()

  val redis = RedisClient()

  val redisBlocking = RedisBlockingClient()

  val r = redis.del("workList").flatMap(_ => {
    consumer()
    publisher()
  })

  Await.result(r, 15 seconds)
  akkaSystem.shutdown()


  def publisher() = {
    redis.lpush("workList", "doSomeWork")
    Thread.sleep(2000)
    redis.rpush("otherKeyWithWork", "doSomeWork1", "doSomeWork2")
  }

  def consumer() = Future {
    val waitWork = 3
    val sequenceFuture = for {i <- 0 to waitWork}
    yield {
      redisBlocking.blpop(Seq("workList", "otherKeyWithWork"), 5 seconds).map(result => {
        result.map(_.map({
          case (key, work) => println(s"list $key has work : ${work.utf8String}")
        }))
      })
    }

    Await.result(Future.sequence(sequenceFuture), 10 seconds)
  }
}
