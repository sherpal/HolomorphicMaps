package renderer

import actions.MathObjectAction
import custommath.Complex
import mathobjects._
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.CanvasRenderingContext2D
import plotcommunication.Message

object PlotWindow {

  val canvas: html.Canvas = dom.document.getElementById("canvas").asInstanceOf[html.Canvas]
  val ctx: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]


  private val _realAxis: (Double, Double) = (-2, 2) // will be a var later (maybe)
  def realAxis: (Double, Double) = _realAxis

  private val _imaginaryAxis: (Double, Double) = (-2, 2) // will be a var later (maybe)
  def imaginaryAxis: (Double, Double) = _imaginaryAxis

  def center: Complex = Complex((realAxis._1 + realAxis._2) / 2, (imaginaryAxis._1 + imaginaryAxis._2) / 2)

  def changeCoordinates(z: Complex, canvasWidth: Int, canvasHeight: Int): Complex = {
    val xUnit = canvasWidth / (realAxis._2 - realAxis._1)
    val yUnit = canvasHeight / (imaginaryAxis._2 - imaginaryAxis._1)

    val topLeft = Complex(realAxis._1, imaginaryAxis._2)
    val offset = ~(z - topLeft)

    Complex(offset.re * xUnit, offset.im * yUnit)
  }

  def canvasToComplexCoordinates(x: Double, y: Double, canvasWidth: Int, canvasHeight: Int): Complex = {
    val xUnit = canvasWidth / (realAxis._2 - realAxis._1)
    val yUnit = canvasHeight / (imaginaryAxis._2 - imaginaryAxis._1)

    val topLeft = Complex(realAxis._1, imaginaryAxis._2)

    topLeft + Complex(x / xUnit, -y / yUnit)
  }


  def drawAxes(): Unit = {
    import Complex.i

    val realAxisStart = changeCoordinates(realAxis._1, canvas.width, canvas.height)
    val realAxisEnd = changeCoordinates(realAxis._2, canvas.width, canvas.height)

    val imagAxisStart = changeCoordinates(imaginaryAxis._1 * i, canvas.width, canvas.height)
    val imagAxisEnd = changeCoordinates(imaginaryAxis._2 * i, canvas.width, canvas.height)

    val unitCircleVertices = (0 to 100)
      .map(_ * 2 * math.Pi / 100)
      .map(Complex.rotation)
      .map(changeCoordinates(_, canvas.width, canvas.height))
      .toList

    ctx.strokeStyle = "rgb(170,170,170)"
    ctx.beginPath()
    ctx.moveTo(realAxisStart.re, realAxisStart.im)
    ctx.lineTo(realAxisEnd.re, realAxisEnd.im)
    ctx.moveTo(imagAxisStart.re, imagAxisStart.im)
    ctx.lineTo(imagAxisEnd.re, imagAxisEnd.im)
    ctx.stroke()

    ctx.beginPath()
    ctx.moveTo(unitCircleVertices.head.re, unitCircleVertices.head.im)
    unitCircleVertices.foreach({ case Complex(x, y) => ctx.lineTo(x, y) })
    ctx.closePath()
    ctx.stroke()
  }

  def drawCanvas(): Unit = {

    ctx.clearRect(0, 0, canvas.width, canvas.height)

    drawAxes()

    MathObject.objects.map(_.canvas).foreach(ctx.drawImage(_, 0, 0, canvas.width, canvas.height))

  }



  def main(args: Array[String]): Unit = {

    Message
    MathObjectCreation

    canvas.width = if (scala.scalajs.LinkingInfo.developmentMode) 400 else dom.window.innerWidth.toInt - 4
    canvas.height = dom.window.innerHeight.toInt + 10


    drawCanvas()

    dom.window.addEventListener("keyup", (event: dom.KeyboardEvent) => {
      if (event.key == "ArrowLeft") {
        MathObjectAction.previousAction()
        drawCanvas()
      } else if (event.key == "ArrowRight") {
        MathObjectAction.nextAction()
        drawCanvas()
      }
    })
  }

}
