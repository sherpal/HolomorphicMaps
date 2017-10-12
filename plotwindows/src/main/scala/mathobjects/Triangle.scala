package mathobjects

import custommath.Complex
import org.scalajs.dom.html
import org.scalajs.dom.raw.CanvasRenderingContext2D
import plotcommunication.TriangleMessage
import renderer.PlotWindow

case class Triangle(v1: Complex, v2: Complex, v3: Complex) extends Ordered[Triangle] {



  def toMessage: TriangleMessage = TriangleMessage(v1, v2, v3)

  val triangleVertices = List(v1, v2, v3, v1)

  val distanceFromCenter: Double = triangleVertices.map(!_).min

  override def compare(that: Triangle): Int = this.distanceFromCenter compare that.distanceFromCenter


  def draw(canvas: html.Canvas, ctx: CanvasRenderingContext2D): Unit = {

    val canvasCoordinates = triangleVertices.tail
      .map(PlotWindow.changeCoordinates(_, canvas.width, canvas.height))

    ctx.beginPath()
    ctx.moveTo(canvasCoordinates.head.re, canvasCoordinates.head.im)
    canvasCoordinates.tail.foreach({ case Complex(x, y) => ctx.lineTo(x, y) })
    ctx.closePath()
    ctx.fill()

  }

  /**
   * Subdivide the triangle in the four triangles formed by the vertices of this triangle, together with the
   * centers of the edges of this triangle.
   *
   * This method is recursive but not tail recursive. This should not be a problem as the pixel area is divided by 4
   * each iteration.
   *
   * @param threshold pixel area limit under which triangle should not divide.
   * @return          the four triangles as a List of Triangles.
   */
  def subDivide(threshold: Double): List[Triangle] = {
    if (shouldIDivide(threshold)) {
      val middles = triangleVertices
        .tail.zip(triangleVertices)
        .map({ case (v1: Complex, v2: Complex) => (v1 + v2) / 2 })

      List(
        (middles.head, middles.tail.head, middles.last),
        (triangleVertices.head, middles.head, middles.last),
        (triangleVertices.tail.head, middles.tail.head, middles.head),
        (triangleVertices.tail.tail.head, middles.tail.tail.head, middles.tail.head)
      )
        .map({ case (v1: Complex, v2: Complex, v3: Complex) => Triangle(v1, v2, v3) })
        .flatMap((t: Triangle) => t.subDivide(threshold))
    } else {
      List(this)
    }
  }

  /**
   * Returns the area of the triangle, in pixels squared.
   */
  private def pixelArea: Double = {
    val realAxis = PlotWindow.realAxis
    val imaginaryAxis = PlotWindow.imaginaryAxis

    val xUnit = PlotWindow.canvas.width / (realAxis._2 - realAxis._1)
    val yUnit = PlotWindow.canvas.height / (imaginaryAxis._2 - imaginaryAxis._1)

    val vertices = triangleVertices
      .map(z => Complex(z.re * xUnit + (realAxis._2 + realAxis._1) / 2,
        z.im * yUnit + (imaginaryAxis._1 + imaginaryAxis._2) / 2))

    math.abs((vertices.tail.head - vertices.head).crossProduct(vertices.tail.tail.head - vertices.tail.head)) / 2
  }

  /**
   * Returns whether the pixel area of the Triangle is bigger than the wished threshold or two vertices are too far from
   * each other, and the triangles bounding box intersect with the visible part of the Complex plane.
   */
  def shouldIDivide(threshold: Double): Boolean =
    (threshold < pixelArea || (v1 - v2).modulus > threshold ||
      (v3 - v2).modulus > threshold || (v1 - v3).modulus > threshold) && {
      val realAxis = PlotWindow.realAxis
      val imaginaryAxis = PlotWindow.imaginaryAxis
      math.max(realAxis._1, boundingBox._2) <= math.min(realAxis._2, boundingBox._4) &&
        math.max(imaginaryAxis._1, boundingBox._3) <= math.min(imaginaryAxis._2, boundingBox._1)
    }

  private val boundingBox: (Double, Double, Double, Double) =
    (
      triangleVertices.map(_.im).max, // top
      triangleVertices.map(_.re).min, // left
      triangleVertices.map(_.im).min, // bottom
      triangleVertices.map(_.re).max  // right
    )

  def contains(z: Complex): Boolean = contains(z.re, z.im)

  def contains(x: Double, y: Double): Boolean = {
    val coef1 = (v3.im - v1.im) * (x - v1.re) - (v3.re - v1.re) * (y - v1.im)
    val coef2 = (v2.re - v1.re) * (y - v1.im) - (v2.im - v1.im) * (x - v1.re)

    val det = (v2 - v1) crossProduct (v3 - v1)

    if (det > 0) {
      coef1 >= 0 && coef2 >= 0 && coef1 + coef2 <= det
    } else {
      coef1 <= 0 && coef2 <= 0 && coef1 + coef2 >= det
    }
  }

}
