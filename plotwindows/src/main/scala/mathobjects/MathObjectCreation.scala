package mathobjects

import actions.{AddObject, AddObjects, MathObjectAction}
import custommath.Complex
import exceptions.WrongDataTypeStored
import globalvariables._
import org.scalajs.dom
import renderer.PlotWindow
import renderer.PlotWindow.canvas

object MathObjectCreation {

  private class DrawAreaDragging(val clicks: Vector[Complex], mode: DrawMode, rgb: (Int, Int, Int)) {
    private val _clickCallback: (Complex) => Option[DrawAreaDragging] = mode match {
      case DrawPolygon() =>
        (z: Complex) => {
          if (endStartDistance(z, startPoint)) {
            if (lastAction.isDefined) {
              MathObjectAction.removeAction(lastAction.get)
            }

            MathObjectAction.addAction(AddObjects(Polygon.polygonsFromVertices(clicks, rgb)))

            canvas.style.cursor = "default"
            lastAction = None
            None
          } else {
            Some(new DrawAreaDragging(
              clicks :+ z, mode, rgb
            ))
          }
        }
      case _ =>
        (z: Complex) => {
          if (endStartDistance(z, startPoint)) {
            MathObjectAction.removeAction(lastAction.get)
          }
          lastAction = None
          None
        }
    }

    def clickCallback(clickPosition: Complex): Option[DrawAreaDragging] = _clickCallback.apply(clickPosition)

    private def changeCursor(currentPos: Complex): Unit = {
      canvas.style.cursor = "crosshair"

      if (endStartDistance(startPoint, currentPos)) {
        mode match {
          case DrawPolygon() =>
            if (clicks.length <= 2) {
              canvas.style.cursor = "no-drop"
            } else {
              canvas.style.cursor = "url(\"validate-green-small.png\"),auto"
            }
          case _ =>
            canvas.style.cursor = "no-drop"
        }
      }
    }


    private val _mouseMoveCallback: (Complex) => Unit =
        (z: Complex) => {
          changeCursor(z)

          if (lastAction.isDefined) {
            MathObjectAction.removeAction(lastAction.get)
          }

          val newAction = this.action(z)
          MathObjectAction.addAction(newAction)
          lastAction = Some(newAction)
    }


    def mouseMoveCallback(mousePosition: Complex): Unit = _mouseMoveCallback(mousePosition)

    def startPoint: Complex = clicks.head

    def action(currentPoint: Complex): MathObjectAction = mode match {
      case DrawLine() =>
        AddObject(new Segment(startPoint, currentPoint, rgb))
      case DrawEllipse() =>
        val center = (currentPoint + startPoint) / 2
        val xRadius = math.abs(currentPoint.re - startPoint.re) / 2
        val yRadius = math.abs(currentPoint.im - startPoint.im) / 2
        AddObject(new Ellipse(center, xRadius, yRadius, rgb))
      case DrawCircle() =>
        val direction = currentPoint - startPoint
        val radius = math.min(math.abs(direction.re / 2), math.abs(direction.im / 2))

        val center = startPoint +  ((direction.re > 0, direction.im > 0) match {
          case (true, true) => radius + Complex.i * radius
          case (true, false) => radius - Complex.i * radius
          case (false, true) => -radius + Complex.i * radius
          case (false, false) => -radius - Complex.i * radius
        })
        AddObject(new Ellipse(center, radius, radius, rgb))
      case DrawRectangle() =>
        val topLeft = Complex(
          math.min(startPoint.re, currentPoint.re),
          math.max(startPoint.im, currentPoint.im)
        )

        val width = math.abs(startPoint.re - currentPoint.re)
        val height = math.abs(startPoint.im - currentPoint.im)

        AddObject(new RectangleShape(topLeft, width, height, rgb))
      case DrawFillEllipse() =>
        val center = (currentPoint + startPoint) / 2
        val xRadius = math.abs(currentPoint.re - startPoint.re) / 2
        val yRadius = math.abs(currentPoint.im - startPoint.im) / 2
        AddObject(new EllipseShape(center, xRadius, yRadius, rgb))
      case DrawFillCircle() =>
        val direction = currentPoint - startPoint
        val radius = math.min(math.abs(direction.re / 2), math.abs(direction.im / 2))

        val center = startPoint +  ((direction.re > 0, direction.im > 0) match {
          case (true, true) => radius + Complex.i * radius
          case (true, false) => radius - Complex.i * radius
          case (false, true) => -radius + Complex.i * radius
          case (false, false) => -radius - Complex.i * radius
        })
        AddObject(new EllipseShape(center, radius, radius, rgb))
      case DrawPolygon() =>
        if (clicks.length == 1) {
          AddObject(new Segment(startPoint, currentPoint, rgb))
        } else {
          AddObjects(Polygon.polygonsFromVertices(clicks :+ currentPoint, rgb))
        }
    }

  }


//  private var lastMathObject: Option[MathObject] = None
  private var lastAction: Option[MathObjectAction] = None
  private var drawAreaDragging: Option[DrawAreaDragging] = None


  private def effectiveMousePos(clientX: Double, clientY: Double): (Double, Double) = {
    val boundingRect = canvas.getBoundingClientRect()
    (clientX - boundingRect.left, clientY - boundingRect.top)
  }

  private def endStartDistance(start: Complex, end: Complex): Boolean = (start - end).modulus2 < 0.05

  canvas.onclick = (event: dom.MouseEvent) => {
    val (x, y) = effectiveMousePos(event.clientX, event.clientY)
    val z = PlotWindow.canvasToComplexCoordinates(x, y, canvas.width, canvas.height)
    drawAreaDragging match {
      case Some(areaDragging) =>
        drawAreaDragging = areaDragging.clickCallback(z)
      case None =>
        drawAreaDragging = Some(new DrawAreaDragging(
          Vector(z),
          DataStorage.retrieveGlobalValue("drawMode") match {
            case m: DrawMode => m
            case data => throw new WrongDataTypeStored(data.getClass.toString)
          },
          DataStorage.retrieveGlobalValue("rgb") match {
            case RGBData(red, green, blue) => (red, green, blue)
            case data => throw new WrongDataTypeStored(data.getClass.toString)
          }
        ))
    }
  }

  canvas.onmousemove = (event: dom.MouseEvent) => {

    drawAreaDragging match {
      case Some(areaDragging) =>
        val (x, y) = effectiveMousePos(event.clientX, event.clientY)
        val z = PlotWindow.canvasToComplexCoordinates(x, y, canvas.width, canvas.height)
        areaDragging.mouseMoveCallback(z)
      case None =>
        canvas.style.cursor = "default"
    }

  }
}
