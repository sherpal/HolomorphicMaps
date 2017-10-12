package renderer



import org.scalajs.dom
import org.scalajs.dom.{Event, html}
import plot.Plot



object MainWindow {



  def main(args: Array[String]): Unit = {

    ColorPicker
    FunctionsMenu
    ChoseMode


    val newPlotButton: html.Button = dom.document.getElementById("newPlot").asInstanceOf[html.Button]

    newPlotButton.addEventListener("click", (_: Event) => {
      Plot()
    })

    dom.document.getElementById("closePlots").asInstanceOf[html.Button].addEventListener("click", (_: dom.MouseEvent) =>
    {
      Plot.plots.values.foreach(_.close())
    })

    dom.document.getElementById("savePngButton").asInstanceOf[html.Button].addEventListener(
      "click",
      (_: dom.MouseEvent) => {
//        dom.window.alert("Not yet implemented.\nComing soon!")
        Plot.focusedPlot match {
          case Some(plot) =>
            plot.saveAsPng()
          case None =>
        }
      }
    )


  }

}
