package mathobjects

import custommath.Complex

/**
 * Rectangle shape to draw on plots.
 */
class RectangleShape(val topLeft: Complex, val width: Double, val height: Double,
                     val rgb: (Int, Int, Int)) extends Shape {

  def drawTriangles: List[Triangle] = List[Triangle](
    Triangle(topLeft, topLeft + width, topLeft - height * Complex.i),
    Triangle(topLeft + width, topLeft - height * Complex.i, topLeft + width - height * Complex.i)
  ).sorted


}
