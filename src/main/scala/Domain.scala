package fa

object Core {
  val FirebaseTld = "firebaseIO.com"
  val Proto = "http://"
  val JSuffix = ".json"

  val Secret = "Firebase_Secret"
}

case class ReadOptions(shallow: Boolean = false, printStyle: PrintStyle)

trait PrintStyle
object PrintStyle {
 case object Pretty extends PrintStyle
  case object Silent extends PrintStyle
  case object Standard extends PrintStyle
}


case class Token(v: String)

trait RestCall {
  val token: Token
  val print: PrintStyle

}

case class Read(token: Token, shallow: Boolean = false, print: PrintStyle) extends RestCall


object Auth {

  import com.firebase.security.token.{TokenGenerator, TokenOptions}
  import scala.collection.JavaConversions._
  import scala.util.Properties

  private lazy val secret = Properties.envOrNone(Core.Secret)

  case class UserId(value: Int)

  case class AuthPayload(userId: UserId)
  private object AuthPayload{

    implicit def asMap(pl: AuthPayload) =
      Map[String, Object](
        "uid" -> pl.userId.value
      )

  }

  def token(p: AuthPayload, admin: Boolean = false): Option[Token] =
    secret.map(s => {
      val tg = new TokenGenerator(s)
      val opts = new TokenOptions()
      opts.setAdmin(admin)

      Token(tg.createToken(mapAsJavaMap(p), opts))
    })

}

//note this stuff is pretty bad
trait WireType[A]
object WireType{
  implicit object i extends WireType[Int]
  implicit object s extends WireType[String]
  implicit object n extends WireType[BigDecimal]
  implicit object b extends WireType[Boolean]
}

object Querying{
  type or[A, B] = Either[A,B]

  case class Query[A,B,C](before: Option[StartAt[A]],
    after:Option[EndAt[B]], 
    equal: Option[EqualTo[C]],  
    ordering: Option[OrderBy],
    limit:  Option[First or Last] )

  object Query {
    def asQueryString[A,B,C](q: Query[A,B,C]): String = {

      for{
        a <- q.after
        b <- q.before
        e <- q.equal
        o <- q.ordering
        l <- q.limit
      } yield {

      }

    }
  }

  trait FirebaseOrdering
  case class OrderBy(key: String) extends FirebaseOrdering
  case object OrderByKey extends FirebaseOrdering
  case object OrderByValue extends FirebaseOrdering
  case object OrderByPriority extends FirebaseOrdering

  case class StartAt[T: WireType](value: T)
  case class EndAt[T: WireType](value: T)
  case class EqualTo[T: WireType](value: T)

  case class First(i: Int)
  case class Last(i: Int)

}

