package plotcommunication

trait MathObjectMessage extends Message {

  val id: Long

  val rgb: ColorMessage

}
