package LabyrintManager


case class Coords(val x: Int, val y: Int) {
   
  

  def neighbor(direction: Direction) = 
        new Coords(this.x + direction.xStep,
               this.y + direction.yStep)

  
  /**
   * Determines whether this pair of coordinates equals the given one.
   * This is the case if both have identical x and y coordinates.
   */
  def ==(another: Coords) =
    this.x == another.x && this.y == another.y

   
  /**
   * Returns a textual description of this pair of coordinates. 
   * The description is of the form `"(x,y)"`.
   */
  override def toString = "(" + this.x + "," + this.y + ")"
    
  
}