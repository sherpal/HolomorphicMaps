package actions

import mathobjects.MathObject

case class AddObjects(newMathObjects: List[MathObject]) extends MathObjectAction {

  def apply(mathObjects: List[MathObject]): List[MathObject] =
    mathObjects ++ newMathObjects

}
