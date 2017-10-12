package mathobjects

import custommath.Complex
import plotcommunication.Message
import plotcommunication.mathobjectmessages.SegmentMessage

/**
 * A Segment is a particular type of [[Line]] that is characterised by a starting point and endpoint.
 */
class Segment(val from: Complex, val to: Complex, rgb: (Int, Int, Int)) extends
  Line(Line.segment(from, to), rgb, false) {

  override def cycle: Boolean = false

  override def toMessage: SegmentMessage = SegmentMessage(Message.newId(), from, to, rgb)

}
