import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, Validate}

object rensing {

  type Checked[T] = Validated[String, T]
  type NotLens[A, B] = A ⇒ Checked[B]

  private def notNull[T](value: T): Checked[T] =
    Option(value) match {
      case Some(b) ⇒ Valid(b)
      case None    ⇒ Invalid("value is null")
    }

  class CheckedLens[A] {
    def apply[B](f: A ⇒ B): A ⇒ Checked[B] = (a: A) ⇒ notNull(f(a))
  }

  class RefinedLens[A, R] {
    def apply[B](f: A ⇒ B)(
        implicit v: Validate[B, R]): (A) ⇒ Validated[String, Refined[B, R]] =
      (a: A) ⇒
        notNull[B](f(a)) match {
          case Valid(value) ⇒
            refineV[R](value) match {
              case Left(err)  ⇒ Invalid(err)
              case Right(err) ⇒ Valid(err)
            }
          case i: Invalid[String] ⇒ i
      }
  }

  class TraverseLens {
    def apply() = ???
  }

  def lens[A]       = new CheckedLens[A]
  def rens[A, R]    = new RefinedLens[A, R]
  def bens[F[_], A] = new TraverseLens

  implicit class LensOps[A, B, F](ab: A ⇒ Checked[B]) {
    def pose[C](bc: NotLens[B, C]): NotLens[A, C] = a ⇒ ab(a) andThen bc
    def ~>[C](bc: NotLens[B, C]): NotLens[A, C]   = pose(bc)
  }
}
