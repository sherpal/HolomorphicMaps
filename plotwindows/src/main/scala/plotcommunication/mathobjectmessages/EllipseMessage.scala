package plotcommunication.mathobjectmessages

import plotcommunication.{ColorMessage, ComplexMessage, MathObjectMessage}

final case class EllipseMessage(id: Long, center: ComplexMessage, xRadius: Double, yRadius: Double,
                                rgb: ColorMessage) extends MathObjectMessage
