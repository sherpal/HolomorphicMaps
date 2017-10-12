package mathobjects
import plotcommunication.{MathObjectMessage, Message, TriangleMessage}
import plotcommunication.mathobjectmessages.RawShapeMessage


/**
 * A Shape is a 2D MathObject.
 *
 * When a Shape is mapped through a function, it is cut into small triangles and each vertex of these triangles are
 * then map through the function to make the image triangle.
 */
trait Shape extends MathObject {

  var threshold: Double = 10

  def drawTriangles: List[Triangle]

  def triangles: List[Triangle] = drawTriangles.flatMap(_.subDivide(threshold))

  def draw(): Unit = {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    ctx.fillStyle = s"rgba(${rgb._1},${rgb._2},${rgb._3},0.9)"
    val distance = 2 * math.sqrt(2)
    drawTriangles.takeWhile(_.distanceFromCenter <= distance).foreach(_.draw(canvas, ctx))
  }

  def toMessage: MathObjectMessage = RawShapeMessage(
    Message.newId(), drawTriangles.map(t => TriangleMessage(t.v1, t.v2, t.v3)), rgb
  )

}
