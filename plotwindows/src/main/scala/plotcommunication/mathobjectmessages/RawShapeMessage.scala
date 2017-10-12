package plotcommunication.mathobjectmessages

import plotcommunication.{ColorMessage, MathObjectMessage, TriangleMessage}

final case class RawShapeMessage(id: Long, triangles: List[TriangleMessage], rgb: ColorMessage)
  extends MathObjectMessage
