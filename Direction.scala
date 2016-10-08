package LabyrintManager



/**
 * The class `Direction` represents compass directions in a grid's coordinate system.
 * There are exactly four instances of this class: `North`, `East`, `South` and `West`;
 * These instances are also defined in the same package.     
 * 
 * All the direction objects are immutable.
 * 
 * @see [[Coords]]   
 * @param xStep the change in x coordinate if one moves one step in this direction. For instance, `West` has an `xStep` of -1 and `North` has an `xStep` of 0.   
 * @param yStep the change in y coordinate if one moves one step in this direction. For instance, `North` has an `yStep` of -1 and `West` has an `yStep` of 0.
 */
sealed abstract class Direction(val xStep: Int, val yStep: Int) { // The word sealed means that you can't directly inherit from this class except within this file. 
                                                                  // Consequently, there are only four objects of type Direction, as defined below.

  /**
   * Returns the next of the four compass directions, moving clockwise from this one. For instance,
   * calling this method on `North` returns `East`.    
   */
  def clockwise = Direction.next(this)

  
  /**
   * Returns the next of the four compass directions, moving counterclockwise from this one. For instance,
   * calling this method on `North` returns `West`.    
   */
  def counterClockwise = Direction.previous(this)

  
  /**
   * Returns a textual description of this direction, that is, the English name of the direction.
   */
  override def toString = this.getClass.getSimpleName.replaceAll("\\$", "")
  
}

// The following is a particular way (in Scala) of creating objects, which is not covered in the present course. 

/**
 * This immutable singleton object represents the northwardly compass direction. It is one of the four predefined instances of class `Direction`.
 * @see [[Direction]]
 */
case object North extends Direction( 0,-1)
/**
 * This immutable singleton object represents the eastwardly compass direction. It is one of the four predefined instances of class `Direction`.
 * @see [[Direction]]
 */
case object East  extends Direction( 1, 0)
/**
 * This immutable singleton object represents the southwardly compass direction. It is one of the four predefined instances of class `Direction`.
 * @see [[Direction]]
 */
case object South extends Direction( 0, 1)
/**
 * This immutable singleton object represents the westwardly compass direction. It is one of the four predefined instances of class `Direction`.
 * @see [[Direction]]
 */
case object West  extends Direction(-1, 0)

case object Lift  extends Direction(0, 0)
/**
 * This companion object of class `Direction` provides a couple of useful constants related to directions.
 * 
 * NOTE TO STUDENTS: The instance variables of this object represent unchanging general ''constants''. In Scala,
 * it is customary to name such constants with an uppercase initial.   
 * 
 * @see the class [[Direction]]
 */
object Direction {
  
  /** a collection of all the four directions, in clockwise order starting with `North` */ 
  val ValuesClockwise = Vector[Direction](North, East, South, West)

  /** The number of the compass directions represented by class `Direction`. Four, that is. */ 
  val Count = this.ValuesClockwise.size

  private val next = this.ValuesClockwise.zip(this.ValuesClockwise.tail ++ this.ValuesClockwise.init).toMap
  private val previous = this.next.map( _.swap )
}

