package actions

import mathobjects.MathObject
import renderer.PlotWindow

trait MathObjectAction {
  def apply(mathObjects: List[MathObject]): List[MathObject]
}


object MathObjectAction {

  private var actionList: List[MathObjectAction] = Nil

  private var _actionNbr: Int = 0
  def actionNbr: Int = _actionNbr

  def addAction(mathObjectAction: MathObjectAction): Unit = {
    actionList = actionList.take(_actionNbr) :+ mathObjectAction
    _actionNbr += 1
    mathObjectAction match {
      case AddObject(o) =>
        o.draw()
        PlotWindow.drawCanvas()
      case AddObjects(os) =>
        os.foreach(_.draw())
        PlotWindow.drawCanvas()
    }
  }

  def addMathObject(mathObject: MathObject): Unit =
    addAction(AddObject(mathObject))

  def addMathObjects(mathObjects: Iterable[MathObject]): Unit =
    addAction(AddObjects(mathObjects.toList))

  def removeObject(mathObject: MathObject): Unit = {
    actionList.indexOf(AddObject(mathObject)) match {
      case -1 =>
        actionList = actionList.map({
          case AddObjects(newMathObjects) =>
            AddObjects(newMathObjects.filterNot(_ == mathObject))
          case action =>
            action
        })
      case idx =>
        actionList = actionList.filterNot(_ == AddObject(mathObject))
        if (idx < _actionNbr) {
          previousAction()
        }
    }
  }

  def removeAction(action: MathObjectAction): Unit = {
    actionList.indexOf(action) match {
      case -1 =>
      case idx =>
        action match {
          case AddObject(o) =>
            MathObject.destroyObject(o)
          case AddObjects(os) =>
            os.foreach(MathObject.destroyObject)
          case _ =>
            if (scala.scalajs.LinkingInfo.developmentMode) {
              println("Nothing to do with that action.")
            }
        }
        actionList = actionList.filterNot(_ == action)
        if (idx < _actionNbr) {
          previousAction()
        }
    }
  }

  def nextAction(): Unit =
    _actionNbr = math.min(actionList.length, _actionNbr + 1)

  def previousAction(): Unit =
    _actionNbr = math.max(0, _actionNbr - 1)

  def currentObjects: List[MathObject] = actionList.take(_actionNbr).foldLeft(List[MathObject]())({
    case (list, action) => action(list)
  })


}
