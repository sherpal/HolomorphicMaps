package mathobjects

import custommath.Complex

class EllipseShape(val center: Complex, val xRadius: Double, val yRadius: Double,
                   val rgb: (Int, Int, Int)) extends Shape {

  /**
   * Returns a list of Triangles that nearly cover the Ellipse.
   *
   * The algorithm is as follows:
   * - start with two triangles whose vertices have angle 0, pi/2 and pi, and pi, 3pi/2 and 2pi
   * - as long as the difference between two angles is too big for the angleThreshold, repeat the following:
   *   for each pair of adjacent angles a1 and a2, form a new [[Triangle]] whose vertices have angles a1, a2 and
   *   (a1 + a2) / 2 and a new List of two pairs of angles (a1, (a1 + a2) / 2) and ((a1 + a2) / 2, a2).
   * This procedure has the advantage that the closer you get to the boundary of the ellipse, the smaller the triangles
   * are.
   */
  def drawTriangles: List[Triangle] = {
    val angleThreshold = math.Pi / 40

    def drawTrianglesAcc(acc: List[Triangle], angles: List[(Double, Double)]): List[Triangle] = {
      if (math.abs(angles.head._1 - angles.head._2) < angleThreshold) {
        acc
      } else {
        val (newTriangles, newAngles): (List[Triangle], List[List[(Double, Double)]]) =
          (for (deltaAngle <- angles) yield {
            (
              Triangle(
                center + Complex(xRadius * math.cos(deltaAngle._1), yRadius * math.sin(deltaAngle._1)),
                center + Complex(xRadius * math.cos(deltaAngle._2), yRadius * math.sin(deltaAngle._2)),
                center + Complex(xRadius * math.cos((deltaAngle._1 + deltaAngle._2) / 2),
                  yRadius * math.sin((deltaAngle._1 + deltaAngle._2) / 2))
              ), List(
              (deltaAngle._1, (deltaAngle._1 + deltaAngle._2) / 2), ((deltaAngle._1 + deltaAngle._2) / 2, deltaAngle._2)
            )
            )
          }).unzip
        drawTrianglesAcc(newTriangles ++ acc, newAngles.flatten)
      }
    }

    drawTrianglesAcc(List[Triangle](), List((0, math.Pi), (math.Pi, 2 * math.Pi))).sorted
  }

}
