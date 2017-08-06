import cats.data.Validated.{Invalid, Valid}
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.auto._
import eu.timepit.refined.collection.NonEmpty
import monocle.{Lens, Optional, Prism}
import monocle.macros.GenLens
import org.scalatest.{MustMatchers, WordSpec}
import rensing._

class LensingSpec extends WordSpec with MustMatchers {

  case class Aye(b: Bee)
  case class Bee(c: See)
  case class See(d: String)

  val aye     = Aye(Bee(See("yay")))
  val badaye  = Aye(null)
  val badaye2 = Aye(Bee(See("")))

  "Lensing" should {
    "train wreck" in {
      aye.b.c.d must be("yay")
    }

    "extract values from a graph" in {

      val ab = lens((_: Aye).b)
      val bc = lens[Bee](_.c)
      val cd = rens[See, NonEmpty](_.d)

      val ad = ab ~> bc ~> cd

      ad(aye) must be(Valid[String Refined NonEmpty]("yay"))
      ad(badaye) must be(Invalid("value is null"))
      ad(badaye2) must be(Invalid("Predicate isEmpty() did not fail."))
    }

    "list things" in {

      def refinedPrism[T, P](
          implicit v: Validate[T, P]): Prism[T, T Refined P] =
        Prism.partial[T, T Refined P] {
          case t if v.isValid(t) => Refined.unsafeApply(t)
        } {
          _.value
        }

      def RefinedLens[A, B, C](s: A ⇒ B)(
          implicit v: Validate[B, C]): Optional[A, Refined[B, C]] =
        Lens[A, B](s)(a ⇒ b ⇒ b).composePrism(refinedPrism[B, C])

      val ab = GenLens[Aye](_.b).asOptional
      val bc = GenLens[Bee](_.c).asOptional
      val cd = RefinedLens[See, String, NonEmpty](_.d)

      val ad = ab composeOptional bc composeOptional cd

      ad.getOption(aye) must be(Some[String Refined NonEmpty]("yay"))
      ad.getOption(badaye2) must be(None)
      ad.getOption(badaye) must be(Some[String Refined NonEmpty]("yay"))

    }

  }

}
