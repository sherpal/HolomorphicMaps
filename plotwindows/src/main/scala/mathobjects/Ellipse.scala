package mathobjects

import custommath.Complex
import plotcommunication.Message
import plotcommunication.mathobjectmessages.EllipseMessage

class Ellipse(val center: Complex, val xRadius: Double, val yRadius: Double,
              rgb: (Int, Int, Int)) extends Line(Line.ellipse(center, xRadius, yRadius), rgb, true) {

  override def toMessage: EllipseMessage = EllipseMessage(Message.newId(), center, xRadius, yRadius, rgb)

}
