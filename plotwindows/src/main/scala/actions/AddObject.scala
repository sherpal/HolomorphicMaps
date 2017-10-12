package actions

import mathobjects.MathObject

case class AddObject(mathObject: MathObject) extends MathObjectAction {

  def apply(mathObjects: List[MathObject]): List[MathObject] = mathObjects :+ mathObject

}
