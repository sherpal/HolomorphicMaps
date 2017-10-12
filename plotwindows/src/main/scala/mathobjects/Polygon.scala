package mathobjects

import custommath.Complex
import plotcommunication.{ComplexMessage, MathObjectMessage, Message}
import plotcommunication.mathobjectmessages.PolygonMessage

class Polygon private (val vertices: Vector[Complex], val rgb: (Int, Int, Int)) extends Shape {

  private val triangulation: List[Triangle] = Polygon.earClipping(vertices).sorted

  def drawTriangles: List[Triangle] = triangulation

  override def toMessage: MathObjectMessage = PolygonMessage(
    Message.newId(), vertices.map(z => ComplexMessage(z.re, z.im)), rgb
  )

}


object Polygon {

  private class Corner(val z: Complex, val next: Complex, val prev: Complex, val cornerIndex: Int) {
    val det: Double = (z - prev).crossProduct(next - z)
    def triangle: Triangle = Triangle(prev, z, next)
    def angle: Double = {
      val arg1 = (prev - z).arg
      val arg2 = (next - z).arg
      (if (arg1 < 0) arg1 + 2 * math.Pi else arg1) - (if (arg2 < 0) arg2 + 2 * math.Pi else arg2)
    }
    def isVertex(v: Complex): Boolean = v == z || v == next || v == prev
  }

  private def earClipping(vertices: Vector[Complex]): List[Triangle] = {
    def earClippingAcc(vs: Vector[Complex], acc: List[Triangle]): List[Triangle] = {
      if (vs.length == 3) Triangle(vs.head, vs(1), vs.last) :: acc
      else {

        val corners = for (j <- vs.indices) yield new Corner(
          vs(j), vs(if (j == vs.length - 1) 0 else j + 1), vs(if (j == 0) vs.length - 1 else j-1), j
        )

        val (convex, reflex) = corners.partition(_.det > 0)
        val ears = convex.filter(v => !reflex.exists(c => (!v.isVertex(c.z)) && v.triangle.contains(c.z.re, c.z.im)))
        val process = ears.minBy(_.angle)

        val (beforeCorner, afterCorner) = vs.splitAt(process.cornerIndex)

        earClippingAcc(beforeCorner ++ afterCorner.tail, process.triangle :: acc)
      }
    }

    earClippingAcc(vertices, List())
  }

  private def areCounterClockwise(vertices: Vector[Complex]): Boolean =
    vertices.zip(vertices.tail :+ vertices.head).zip(vertices.tail.tail ++ Vector(vertices(0), vertices(1)))
    .map({ case ((v1, v2), v3) => (v2 - v1) crossProduct (v3 - v1)})
    .sum > 0


  /**
   * Returns the intersection of the segments [x1,x2] and [y1,y2] if it exists, and None otherwise.
   */
  private def segmentIntersection(x1: Complex, x2: Complex, y1: Complex, y2: Complex): Option[Complex] = {
    val v1 = x2 - x1
    val v2 = y2 - y1
    val z = y1 - x1
    val det = - v1.re * v2.im + v2.re * v1.im

    if (det == 0)
      None
    else {
      val t1 = (- v2.im * z.re + v2.re * z.im) / det
      val t2 = (- v1.im * z.re + v1.re * z.im) / det

      if (t1 >= 0 && t1 <= 1 && t2 >= 0 && t2 <= 1) {
        Some(x1 + t1 * v1)
      } else
        None
    }
  }


  /**
   * Given a vector of vertices, it returns a List of all the polygons created by joining the vertices given as
   * argument.
   *
   * If two edges intersect, then there is more than one
   */
  def polygonsFromVertices(vertices: Vector[Complex], rgb: (Int, Int, Int)): List[Polygon] = {

    if (vertices.length < 3) {
      Nil
    } else {
      def accumulatorFct(polygons: List[Polygon], verticesSets: List[Vector[Complex]]): List[Polygon] = {

        if (verticesSets.isEmpty) {
          polygons
        } else {
          val polygonsAndSubVertices: List[(Option[Polygon], List[Vector[Complex]])] = verticesSets.map(vertices => {
            vertices.indices.flatMap(j => (j + 2 until vertices.length + j - 1).map((j, _)))
              .map({ case (j, k) => (j, k % vertices.length) })
              .map({ case (j, k) => (j, k, segmentIntersection(
                vertices(j), vertices((j + 1) % vertices.length), vertices(k), vertices((k + 1) % vertices.length)
              ))})
              .find(_._3.isDefined) match {
              case Some((j, k, Some(intersection))) =>
                val (vertexSet1, vertexSet2) = vertices.splitAt(math.min(j, k) + 1)
                (
                  None,
                  List(
                    intersection +: (vertexSet2.drop(math.abs(k - j)) ++ vertexSet1),
                    intersection +: vertexSet2.take(math.abs(k - j))

                  )
                )
              case _ =>
                (Some(Polygon(vertices, rgb)), Nil) // no intersection
            }

          })

          val (newPolygonsOption, newVerticesOption) = polygonsAndSubVertices.unzip

          accumulatorFct(
            newPolygonsOption.filter(_.isDefined).map(_.get) ++ polygons,
            newVerticesOption.flatten
          )

        }
      }

      accumulatorFct(Nil, List(vertices))
    }
  }



  def regularPolygon(center: Complex, nbrSides: Int, radius: Double, rotation: Double,
                     rgb: (Int, Int, Int)): Polygon = new Polygon(
    (0 until nbrSides)
      .map(j => radius * Complex.rotation(rotation + 2 * math.Pi * j / nbrSides)).toVector
      .map(_ + center),
    rgb
  )


  def apply(vertices: Vector[Complex], rgb: (Int, Int, Int)): Polygon = new Polygon(
    if (areCounterClockwise(vertices)) vertices else vertices.reverse, rgb
  )



}