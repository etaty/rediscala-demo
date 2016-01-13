import akka.util.ByteString
import redis.api.keys.Exists
import redis.{ByteStringSerializer, RedisClient, ByteStringFormatter}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class DumbClass(s1: String, s2: String)

object DumbClass {
  implicit val byteStringFormatter = new ByteStringFormatter[DumbClass] {
    def serialize(data: DumbClass): ByteString = {
      ByteString(data.s1 + "|" + data.s2)
    }

    def deserialize(bs: ByteString): DumbClass = {
      val r = bs.utf8String.split('|').toList
      DumbClass(r(0), r(1))
    }
  }
}


case class PrefixedKey[K: ByteStringSerializer](prefix: String, key: K)

object PrefixedKey {
  implicit def serializer[K](implicit redisKey: ByteStringSerializer[K]) = new ByteStringSerializer[PrefixedKey[K]] {
    def serialize(data: PrefixedKey[K]): ByteString = {
      ByteString(data.prefix) ++ redisKey.serialize(data.key)
    }
  }
}


object ExampleByteStringFormatter extends App {
  implicit val akkaSystem = akka.actor.ActorSystem()

  val redis = RedisClient()

  val dumb = DumbClass("s1", "s2")

  val r = for {
    set <- redis.set("dumbKey", dumb)
    getDumbOpt <- redis.get[DumbClass]("dumbKey")
  } yield {
    getDumbOpt.map(getDumb => {
      assert(getDumb == dumb)
      println(getDumb)
    })
  }

  Await.result(r, 5 seconds)


  val prefixedKey = PrefixedKey("prefix", ByteString("1"))

  val exists = redis.send(Exists(prefixedKey))

  val bool = Await.result(exists, 5 seconds)
  assert(!bool)

  akkaSystem.shutdown()
}

