package plotcommunication

final case class ColorMessage(red: Int, green: Int, blue: Int) extends Message

object ColorMessage {
  import scala.language.implicitConversions

  implicit def fromRGB(rgb: (Int, Int, Int)): ColorMessage = ColorMessage(rgb._1, rgb._2, rgb._3)

}

