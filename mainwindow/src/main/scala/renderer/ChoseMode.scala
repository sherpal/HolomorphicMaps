package renderer

import globalvariables._
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.{CanvasRenderingContext2D, MouseEvent}



object ChoseMode {

  private val buttonNameToDrawMode: Map[String, DrawMode] = Map(
    "lineButton" -> DrawLine(),
    "ellipseButton" -> DrawEllipse(),
    "circleButton" -> DrawCircle(),
    "rectangleFilledButton" -> DrawRectangle(),
    "ellipseFilledButton" -> DrawFillEllipse(),
    "circleFilledButton" -> DrawFillCircle(),
    "polygonButton" -> DrawPolygon()
  )

  private var focused: html.Element = dom.document.getElementById("modeButtonsContainer")
    .children(0).children(0).asInstanceOf[html.Canvas]

  private def focus(elem: html.Element): Unit = {
    focused.style.border = "2px solid black"

    focused = elem
    focused.style.border = "2px solid red"

    DataStorage.storeGlobalValue("drawMode", buttonNameToDrawMode(focused.id))

    println(focused.id)
  }

  focus(focused)

  private val modeButtonsList: List[html.Canvas] =
    (for (j <- 0 until dom.document.getElementById("modeButtonsContainer").children.length) yield {
      dom.document.getElementById("modeButtonsContainer").children(j).children(0).asInstanceOf[html.Canvas]
    }).toList

  modeButtonsList.foreach(child => {
    child.addEventListener("click", (_: MouseEvent) => {
      focus(child)
    })
  })

  private val lineButton = dom.document.getElementById("lineButton").asInstanceOf[html.Canvas]
  private val lineButtonCtx = lineButton.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  lineButtonCtx.fillStyle = "rgb(170,170,170)"
  lineButtonCtx.strokeStyle = "black"
  lineButtonCtx.lineWidth = 2
  lineButtonCtx.fillRect(0, 0, lineButton.width, lineButton.height)
  lineButtonCtx.moveTo(5, 5)
  lineButtonCtx.lineTo(lineButton.width - 5, lineButton.height - 5)
  lineButtonCtx.stroke()

  private val ellipseButton = dom.document.getElementById("ellipseButton").asInstanceOf[html.Canvas]
  private val ellipseButtonCtx = ellipseButton.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  ellipseButtonCtx.fillStyle = "rgb(170,170,170)"
  ellipseButtonCtx.strokeStyle = "black"
  ellipseButtonCtx.lineWidth = 2
  ellipseButtonCtx.fillRect(0, 0, ellipseButton.width, ellipseButton.height)
  ellipseButtonCtx.beginPath()
  ellipseButtonCtx.moveTo(ellipseButton.width - 5, ellipseButton.height / 2)
  (1 until 100).map(_ * 2 * math.Pi / 100).map(angle => (math.cos(angle), math.sin(angle)))
    .map({ case (x, y) => (x * (ellipseButton.width / 2 - 5), y * (ellipseButton.height / 2 - 10)) })
    .map({ case (x, y) => (x + ellipseButton.width / 2, y + ellipseButton.height / 2) })
    .foreach({ case (x, y) => ellipseButtonCtx.lineTo(x, y) })
  ellipseButtonCtx.closePath()
  ellipseButtonCtx.stroke()

  private val circleButton = dom.document.getElementById("circleButton").asInstanceOf[html.Canvas]
  private val circleButtonCtx = circleButton.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  circleButtonCtx.fillStyle = "rgb(170,170,170)"
  circleButtonCtx.strokeStyle = "black"
  circleButtonCtx.lineWidth = 2
  circleButtonCtx.fillRect(0, 0, circleButton.width, circleButton.height)
  circleButtonCtx.beginPath()
  circleButtonCtx.arc(circleButton.width / 2, circleButton.height / 2, circleButton.width / 2 - 5, 0, 2 * math.Pi)
  circleButtonCtx.closePath()
  circleButtonCtx.stroke()

  private val rectangleButton = dom.document.getElementById("rectangleFilledButton").asInstanceOf[html.Canvas]
  private val rectangleButtonCtx = rectangleButton.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  rectangleButtonCtx.fillStyle = "rgb(170,170,170)"
  rectangleButtonCtx.fillRect(0, 0, rectangleButton.width, rectangleButton.height)
  rectangleButtonCtx.fillStyle = "black"
  rectangleButtonCtx.fillRect(5, 10, rectangleButton.width - 10, rectangleButton.height - 20)


  private val ellipseFillButton = dom.document.getElementById("ellipseFilledButton").asInstanceOf[html.Canvas]
  private val ellipseFillButtonCtx = ellipseFillButton.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  ellipseFillButtonCtx.fillStyle = "rgb(170,170,170)"
  ellipseFillButtonCtx.fillRect(0, 0, ellipseFillButton.width, ellipseFillButton.height)
  ellipseFillButtonCtx.beginPath()
  ellipseFillButtonCtx.moveTo(ellipseFillButton.width - 5, ellipseFillButton.height / 2)
  (1 until 100).map(_ * 2 * math.Pi / 100).map(angle => (math.cos(angle), math.sin(angle)))
    .map({ case (x, y) => (x * (ellipseFillButton.width / 2 - 5), y * (ellipseFillButton.height / 2 - 10)) })
    .map({ case (x, y) => (x + ellipseFillButton.width / 2, y + ellipseFillButton.height / 2) })
    .foreach({ case (x, y) => ellipseFillButtonCtx.lineTo(x, y) })
  ellipseFillButtonCtx.closePath()
  ellipseFillButtonCtx.fillStyle = "black"
  ellipseFillButtonCtx.fill()


  private val circleFillButton = dom.document.getElementById("circleFilledButton").asInstanceOf[html.Canvas]
  private val circleFillButtonCtx = circleFillButton.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  circleFillButtonCtx.fillStyle = "rgb(170,170,170)"
  circleFillButtonCtx.fillRect(0, 0, circleFillButton.width, circleFillButton.height)
  circleFillButtonCtx.fillStyle = "black"
  circleFillButtonCtx.beginPath()
  circleFillButtonCtx.arc(
    circleFillButton.width / 2, circleFillButton.height / 2, circleFillButton.width / 2 - 5, 0, 2 * math.Pi
  )
  circleFillButtonCtx.closePath()
  circleFillButtonCtx.fill()


  private val polygonButton = dom.document.getElementById("polygonButton").asInstanceOf[html.Canvas]
  private val polygonButtonCtx = polygonButton.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  polygonButtonCtx.fillStyle = "rgb(170,170,170)"
  polygonButtonCtx.fillRect(0, 0, polygonButton.width, polygonButton.height)
  polygonButtonCtx.fillStyle = "black"
  polygonButtonCtx.beginPath()
  polygonButtonCtx.moveTo(10, 20)
  polygonButtonCtx.lineTo(15, 5)
  polygonButtonCtx.lineTo(35, 15)
  polygonButtonCtx.lineTo(23, 22)
  polygonButtonCtx.lineTo(30, 35)
  polygonButtonCtx.lineTo(12, 30)
  polygonButtonCtx.closePath()
  polygonButtonCtx.fill()

}
