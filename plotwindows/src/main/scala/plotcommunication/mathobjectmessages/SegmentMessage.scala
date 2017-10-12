package plotcommunication.mathobjectmessages

import plotcommunication.{ColorMessage, ComplexMessage, MathObjectMessage}

final case class SegmentMessage(id: Long, from: ComplexMessage, to: ComplexMessage, rgb: ColorMessage)
  extends MathObjectMessage
