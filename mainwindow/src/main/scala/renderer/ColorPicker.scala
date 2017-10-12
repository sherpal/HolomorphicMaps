package renderer

import globalvariables.{DataStorage, RGBData}
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.{CanvasRenderingContext2D, Event}

object ColorPicker {

  val redSlider: html.Input = dom.document.getElementById("redSlider").asInstanceOf[html.Input]
  val greenSlider: html.Input = dom.document.getElementById("greenSlider").asInstanceOf[html.Input]
  val blueSlider: html.Input = dom.document.getElementById("blueSlider").asInstanceOf[html.Input]

  def rgb: (Int, Int, Int) = (redSlider.value.toInt, greenSlider.value.toInt, blueSlider.value.toInt)

  val selectedColorCanvas: html.Canvas = dom.document.getElementById("selectedColor").asInstanceOf[html.Canvas]
  private val ctx: CanvasRenderingContext2D =
    selectedColorCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  private def changeSelectedColorCanvasColor(): Unit = {
    ctx.fillStyle = s"rgb(${rgb._1},${rgb._2},${rgb._3}"
    ctx.fillRect(0, 0, selectedColorCanvas.width, selectedColorCanvas.height)

    DataStorage.storeGlobalValue("rgb", RGBData(rgb._1, rgb._2, rgb._3))
  }

  changeSelectedColorCanvasColor()

  List(redSlider, greenSlider, blueSlider)
    .foreach(input => {
      input.addEventListener("change", (_: Event) => {
        changeSelectedColorCanvasColor()
      })
    })

}
