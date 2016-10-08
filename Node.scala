package LabyrintManager

trait Node {
  def isBarrier: Boolean
}

class Floor extends Node {
  def isBarrier = false
  override def toString = {
    "@"
  }
}

class Barrier(val value: Int, val coords: Coords) extends Node {
  def <(another: Barrier) = this.value< another.value
  def >(another: Barrier) = this.value > another.value
  def isBarrier = true
  override def toString = {
    value.toString
  }
}

class Player(val coords: Coords) extends Node {
  def isBarrier = false
}

class Goal(val coords: Coords) extends Node {
  def isBarrier = false
}

class Teleport(val coords: Coords) extends Node {
  def isBarrier = false
}