package custommath


class HolomorphicMap(val name: String, val call: String,
                     val f: (Complex) => Complex,
                     val cut: Option[String] = None,
                     val singularities: List[Complex] = List()) {

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: HolomorphicMap => that.name == this.name || that.call == this.call
    case _ => false
  }

  override def hashCode(): Int = (name + call).hashCode
}


object HolomorphicMap {
  def apply(name: String, call: String,
            f: (Complex) => Complex,
            cut: Option[String] = None,
            singularities: List[Complex] = List()): HolomorphicMap = new HolomorphicMap(
    name, call, f, cut, singularities
  )

  import Complex.i

  val holomorphicMaps: List[(String, HolomorphicMap)] = List(
    HolomorphicMap("Identity z", "id", z => z),
    HolomorphicMap("Shift Right z+1", "shiftright", z => z + 1),
    HolomorphicMap("Shift Left z-1", "shiftleft", z => z - 1),
    HolomorphicMap("Flip -z", "minus", z => -z),
    HolomorphicMap("Quarter rotation i*z", "itimes", z => Complex.i * z),
    HolomorphicMap("Height rotation", "heigthtimes", z => Complex.rotation(math.Pi / 4) * z),
    HolomorphicMap("Square z^2", "sqr", z => z * z),
    HolomorphicMap("Cube z^3", "cube", z => z^3),
    HolomorphicMap("Square root z^(1/2)", "sqrt", z => z^0.5, Some("(-Inf,0]")),
    HolomorphicMap("V(z-1)V(z+1)", "doublesqrt", z => (z-1)^0.5 * (z+1)^0.5, Some("[-1,1]")),
    HolomorphicMap("Exponential", "exp", z => Complex.exp(z)),
    HolomorphicMap("Logarithm", "log", z => Complex.log(z), Some("(-Inf,0]")),
    HolomorphicMap("Inverse 1/z", "inverse", z => 1 / z, singularities = List(0)),
    HolomorphicMap("Inverse 1/(z-1)", "inverseshifted", z => 1 / (z - 1), singularities = List(1)),
    HolomorphicMap("Möbius (z-1)/(z+1)", "simplemobius", z => (z - 1) / (z + 1), singularities = List(-1)),
    HolomorphicMap("Möbius (z-i)/(z+i)", "simplemobius2", z => (z - i) / (z + i), singularities = List(-i)),
    HolomorphicMap("Rational (z+1/z)/2", "extoriorinteriorunit", z => (z + 1 / z) / 2, singularities = List(0)),
    HolomorphicMap("sin", "sin", z => Complex.sin(z)),
    HolomorphicMap("cos", "cos", z => Complex.cos(z))
  ).map(map => map.name -> map)

  val holomorphicMapsMap: Map[String, HolomorphicMap] = holomorphicMaps.toMap

}
