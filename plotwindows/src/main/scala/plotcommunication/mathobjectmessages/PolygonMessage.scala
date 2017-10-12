package plotcommunication.mathobjectmessages

import plotcommunication.{ColorMessage, ComplexMessage, MathObjectMessage}

final case class PolygonMessage(id: Long, vertices: Vector[ComplexMessage], rgb: ColorMessage) extends MathObjectMessage

