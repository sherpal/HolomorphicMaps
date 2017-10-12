package plotcommunication

import custommath.Complex

final case class ComplexMessage(re: Double, im: Double) extends Message

object ComplexMessage {
  import scala.language.implicitConversions

  implicit def fromComplex(z: Complex): ComplexMessage = ComplexMessage(z.re, z.im)
}

