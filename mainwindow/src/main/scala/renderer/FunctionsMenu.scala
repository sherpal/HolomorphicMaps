package renderer

import custommath.HolomorphicMap
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent
import plot.Plot

object FunctionsMenu {


  private val container: html.Div = dom.document.getElementById("functionsContainer").asInstanceOf[html.Div]

  private var selectedFunction: html.Anchor = _

  val functionButtons: List[html.Anchor] = HolomorphicMap.holomorphicMaps.map(_._2).map(function => {
    val a = dom.document.createElement("a").asInstanceOf[html.Anchor]

    a.textContent = function.name

    a.classList.add("functionButton")

    a.addEventListener("click", (_: MouseEvent) => {
      focusButton(a)
    })

    container.appendChild(a)
    a
  })

  private def focusButton(anchor: html.Anchor): Unit = {
    selectedFunction.className = "functionButton"
    selectedFunction = anchor
    selectedFunction.classList.add("active")
  }

  selectedFunction = functionButtons.head
  focusButton(selectedFunction)


  val mapButton: html.Button = dom.document.getElementById("mapButton").asInstanceOf[html.Button]

  mapButton.addEventListener("click", (_: MouseEvent) => {

    Plot.focusedPlot match {
      case Some(plot) => plot.map(selectedFunction.textContent)
      case None => dom.window.alert("No selected plot.")
    }

  })

}
