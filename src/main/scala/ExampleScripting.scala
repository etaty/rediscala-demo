import redis.api.scripting.RedisScript
import redis.protocol.{Bulk, MultiBulk}
import redis.RedisClient
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

object ExampleScripting extends App {
  implicit val akkaSystem = akka.actor.ActorSystem()

  val redis = RedisClient()

  val redisScript = RedisScript("return 'rediscala'")
  val redisScriptKeysArgs = RedisScript("return {redis.call('get', KEYS[1]),ARGV[1]}")

  val set = redis.set("key", "scripting")

  val script = redis.evalshaOrEval(redisScriptKeysArgs, Seq("key"), Seq("arg"))

  val scriptString = redis.evalshaOrEval(redisScript)

  val r = for {
    _ <- set
    _scriptString <- scriptString
    _script <- script
  } yield {
    println(s"script ${redisScript.script} :")
    val bulk = _scriptString.asInstanceOf[Bulk]
    println(bulk.toString())

    println(s"script ${redisScriptKeysArgs.script} :")
    val multibulk = _script.asInstanceOf[MultiBulk]
    multibulk.responses.map(_.map(reply => {
      println(reply.toByteString.utf8String)
    }))
  }
  Await.result(r, 5 seconds)

  akkaSystem.shutdown()
}
