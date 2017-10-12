package mathobjects

/**
 * RawShapes are created by mapping rectangular of disk shapes via functions.
 */
class RawShape(shapeTriangles: List[Triangle], val rgb: (Int, Int, Int)) extends Shape {

  def drawTriangles: List[Triangle] = shapeTriangles

}
