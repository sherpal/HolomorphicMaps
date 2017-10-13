package plot


import electron.{BrowserWindow, BrowserWindowOptions}
import io.IO
import nodejs.Path

import scala.collection.mutable
import scala.scalajs.js.UndefOr

class Plot private {

  private val window: BrowserWindow = new BrowserWindow(new BrowserWindowOptions {

    override val width: UndefOr[Int] = Plot.plotWidth
    override val height: UndefOr[Int] = Plot.plotHeight

    override val resizable: UndefOr[Boolean] = false

    override val icon: UndefOr[String] = Path.join(
      Path.dirname(IO.baseDirectory), "/assets/icon/icon.ico"
    )
  })

  window.loadURL(Path.join(io.IO.baseDirectory, "/plotwindows/html/index.html"))

  if (scala.scalajs.LinkingInfo.developmentMode)
    window.webContents.openDevTools()

  window.setMenu(null)

  window.webContents.once("did-finish-load", () => {
    Plot._plots += (window.id -> this)
    focus()
    BrowserWindow.getAllWindows().toList.foreach(w => {
        w.emit("ready-to-draw", window.id)
      })
  })


  private def focus(): Unit = {
    Plot._focusedPlot.foreach(_.blur())
    Plot._focusedPlot = Some(this)
    window.webContents.send("focus")
  }

  private def blur(): Unit = {
    window.webContents.send("blur")
  }

  window.on("focus", () => {
    focus()
  })

  window.on("close", () => {
    if (isFocusedPlot) {
      Plot._focusedPlot = None
    }

    Plot._plots.find(_._2 == this) match {
      case Some((id, _)) =>
        Plot._plots.remove(id)
      case None =>
    }
  })

  def id: Int = window.id

  def isFocusedPlot: Boolean = Plot.focusedPlot.isDefined && Plot.focusedPlot.get == this

  def map(functionName: String): Plot = {

    window.once("ready-to-draw", (id: Int) => {
      window.webContents.send("map", id, functionName)
    })

    Plot()
  }


  def close(): Unit = window.close()


  def saveAsPng(): Unit =
    window.webContents.send("save-as-png")
}


object Plot {

  private val _plots: mutable.Map[Int, Plot] = mutable.Map()

  private var _focusedPlot: Option[Plot] = None

  def focusedPlot: Option[Plot] = _focusedPlot

  def plots: Map[Int, Plot] = _plots.toMap

  val plotWidth: Int = if (scala.scalajs.LinkingInfo.developmentMode) 800 else 400
  val plotHeight: Int = 400

  def apply(): Plot = {
    val newPlot = new Plot

    newPlot
  }

}