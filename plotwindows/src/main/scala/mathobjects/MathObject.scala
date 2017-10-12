package mathobjects

import actions.MathObjectAction
import custommath.{Complex, HolomorphicMap}
import exceptions.MessageToObjectNotImplemented
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.CanvasRenderingContext2D
import plotcommunication.mathobjectmessages._
import plotcommunication._
import renderer.PlotWindow

import scala.collection.mutable


/**
 * A MathObject represents any drawable object on a Plot, such as
 * - shapes
 * - lines
 * - axis and unit circle
 */
trait MathObject {

  def draw(): Unit

  def visible: Boolean = MathObject.objects.contains(this)

  val rgb: (Int, Int, Int)

  private lazy val canvasAndCtx: (html.Canvas, CanvasRenderingContext2D) = MathObject.askCanvas()

  def canvas: html.Canvas = canvasAndCtx._1

  def ctx: CanvasRenderingContext2D = canvasAndCtx._2


  def toMessage: Message

}

object MathObject {

  import scala.language.implicitConversions
  implicit def fromComplexMessage(z: ComplexMessage): Complex = Complex(z.re, z.im)
  implicit def fromColorMessage(colorMessage: ColorMessage): (Int, Int, Int) =
    (colorMessage.red, colorMessage.green, colorMessage.blue)
  implicit def fromTriangleMessage(triangleMessage: TriangleMessage): Triangle =
    Triangle(triangleMessage.v1, triangleMessage.v2, triangleMessage.v3)

  def fromMessage(message: MathObjectMessage): MathObject = message match {
    case SegmentMessage(_, from, to, rgb) =>
      new Segment(from, to, rgb)
    case EllipseMessage(_, center, xRadius, yRadius, rgb) =>
      new Ellipse(center, xRadius, yRadius, rgb)
    case LineMessage(_, points, isCycle, rgb) =>
      new Line(points.map(fromComplexMessage), rgb, isCycle = isCycle)
    case RawShapeMessage(_, triangles, rgb) =>
      new RawShape(triangles.map(fromTriangleMessage), rgb)
    case PolygonMessage(_, vertices, rgb) =>
      Polygon(vertices.map(fromComplexMessage), rgb)
    case _ =>
      throw new MessageToObjectNotImplemented(message.getClass.toString)
  }

  private var allObjects: List[MathObject] = Nil

  def objects: List[MathObject] = MathObjectAction.currentObjects

  def addObject(mathObject: MathObject): Unit = {
    allObjects :+= mathObject
    MathObjectAction.addMathObject(mathObject)
    mathObject.draw()
    PlotWindow.drawCanvas()
  }

  def addObjects(mathObjects: Iterable[MathObject]): Unit = {
    allObjects ++= mathObjects
    MathObjectAction.addMathObjects(mathObjects)
    mathObjects.foreach(_.draw())
    PlotWindow.drawCanvas()
  }

  def destroyObject(mathObject: MathObject): Unit = {
    allObjects = allObjects.filterNot(_ == mathObject)
    MathObjectAction.removeObject(mathObject)
    storeCanvas(mathObject.canvasAndCtx)
    PlotWindow.drawCanvas()
  }

  def mapObject(mathObject: MathObject, holomorphicMap: HolomorphicMap): MathObject = mathObject match {
    case mathObject: Line =>
      new Line(mathObject.points.map(holomorphicMap.f), mathObject.rgb, mathObject.cycle)
    case mathObject: Shape =>
      val f = holomorphicMap.f
      new RawShape(
        mathObject.triangles
          .filterNot(triangle => holomorphicMap.singularities.exists(triangle.contains))
          .map({ case Triangle(v1, v2, v3) => Triangle(f(v1), f(v2), f(v3)) })
          .sorted,
        mathObject.rgb
      )
  }

  val canvasSize: Int = 500

  private val canvasBag: mutable.Queue[(html.Canvas, CanvasRenderingContext2D)] = mutable.Queue()


  private def askCanvas(): (html.Canvas, CanvasRenderingContext2D) = {
    val (canvas, ctx) = if (canvasBag.isEmpty) {
      if (scala.scalajs.LinkingInfo.developmentMode) {
        println("need a new canvas")
      }
      val canvas = dom.document.createElement("canvas").asInstanceOf[html.Canvas]
      val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      (canvas, ctx)
    } else {
      canvasBag.dequeue()
    }
    canvas.width = canvasSize
    canvas.height = canvasSize
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    (canvas, ctx)
  }

  private def storeCanvas(canvas: html.Canvas, ctx: CanvasRenderingContext2D): Unit =
    canvasBag.enqueue((canvas, ctx))

  private def storeCanvas(tuple: (html.Canvas, CanvasRenderingContext2D)): Unit = storeCanvas(tuple._1, tuple._2)

}

