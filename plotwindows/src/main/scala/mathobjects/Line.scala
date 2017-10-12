package mathobjects

import custommath.Complex
import plotcommunication.{ComplexMessage, MathObjectMessage, Message}
import plotcommunication.mathobjectmessages.LineMessage
import renderer.PlotWindow

class Line(val points: Vector[Complex], val rgb: (Int, Int, Int), isCycle: Boolean = false)
  extends MathObject {


  /** Returns whether the points of the line are close enough to those of a straight line linking the extremities. */
  def isNearSegment: Boolean = {
    points.isEmpty || points.tail.isEmpty || points
      .zip(Line.segment(points.head, points.last, points.length))
      .map({case (z1, z2) => (z1 - z2).modulus2}).max < 0.1
  }

  /** A Line with cycle set to true will link the last point to the first one when drawing. */
  private val _cycle: Boolean = isCycle
  def cycle: Boolean = _cycle


  def draw(): Unit = {

    val canvasCoordinates = points.map(PlotWindow.changeCoordinates(_, canvas.width, canvas.height))

    ctx.clearRect(0, 0, canvas.width, canvas.height)

    ctx.lineWidth = 3
    ctx.strokeStyle = s"rgb(${rgb._1},${rgb._2},${rgb._3})"
    ctx.beginPath()
    ctx.moveTo(canvasCoordinates.head.re, canvasCoordinates.head.im)
    canvasCoordinates.tail.foreach({ case Complex(x, y) => ctx.lineTo(x, y) })
    if (cycle) {
      ctx.closePath()
    }

    ctx.stroke()

  }

  def toMessage: MathObjectMessage = LineMessage(
    Message.newId(), points.map(z => ComplexMessage(z.re, z.im)), cycle, rgb
  )

}


object Line {

  /**
   * Returns a [[Vector]] of [[Complex]] numbers on a straight line from "from" to "to".
   *
   * @param from      starting point of the segment.
   * @param to        ending point of the segment.
   * @param numPoints the number of sample points to take (must be bigger than or equal to 2).
   * @return          a vector of all the complex points in the segment.
   */
  def segment(from: Complex, to: Complex, numPoints: Int = 100): Vector[Complex] = {
    val step = (to - from) / (numPoints - 1)
    (for (j <- 0 until numPoints) yield from + j * step).toVector
  }

  def ellipse(center: Complex, xRadius: Double, yRadius: Double, numPoints: Int = 100): Vector[Complex] = {
    (0 until numPoints)
      .map(_ * 2 * math.Pi / numPoints)
      .map(Complex.rotation)
      .map({ case Complex(x, y) => Complex(xRadius * x, yRadius * y) })
      .map(_ + center)
      .toVector
  }
}
