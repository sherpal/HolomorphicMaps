package plotcommunication.mathobjectmessages

import plotcommunication.{ColorMessage, ComplexMessage, MathObjectMessage}

final case class LineMessage(id: Long, points: Vector[ComplexMessage], isCycle: Boolean,
                             rgb: ColorMessage) extends MathObjectMessage
