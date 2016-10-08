package LabyrintManager

import scala.collection.mutable.Buffer
import scala.reflect.ClassTag
import scala.util.Random

/**
 * The class `Grid` represents rectangular grids that contain elements of a particular kind.
 * Each element in a grid is located at a unique pair of coordinates.
 * 
 * There are different kinds of grids: the type of element that a grid contains is defined
 * by a type parameter. For instance `Grid[Glass]` is a grid where each pair of x and y coordinates
 * contains a `Glass` object, and `Grid[Square]` is a grid containing `Square` objects.
 * 
 * X coordinates run from 0 to `width-1`, y coordinates from 0 to `height-1`. (0, 0) corresponds
 * to the upper left corner of the grid. 
 * 
 * This class does not initialize the elements of a newly created grid. Subclasses may initialize
 * the elements of the grid in whichever way is appropriate for the subclass.
 * 
 * @param width  the number of elements in each row of the grid
 * @param height the number of elements in each column of the grid 
 */
class Labyrint(map: Array[Array[Node]]) {

  private val labyrint = this.map
  var playerCoord: Coords = new Coords(0,0)
  var goalCoord: Coords = null
  var moves = 0
  
  def apply(c : Coords): Node = elementAt(c)
  
  def elementAt(location: Coords): Node = {
    if(this.contains(location)) {
      this.labyrint(location.x)(location.y)   
    } else {
      val newLocation = convertCoords(location)
      this.labyrint(newLocation.x)(newLocation.y)
    }
  }
  
  var aikaisempi: Node = new Floor
  
  
  def movePlayer(direction: Direction): Boolean = {
    var maalissa = false
    direction match {
      case Lift => {
        val coord = addTeleportCoords(playerCoord)
        if(coord.isDefined) {
        	
          if(!isBarrier(coord.get)) {
            maalissa = elementAt(coord.get).isInstanceOf[Player]
            moves += 1
            update(playerCoord, aikaisempi)
            aikaisempi = elementAt(coord.get)
            update(coord.get, new Player(coord.get))
            playerCoord = coord.get
          }
        }
      } 
      case _ => {
        val coord = playerCoord.neighbor(direction)
        if(!isBarrier(coord)) {
            maalissa = elementAt(coord).isInstanceOf[Player]
            moves += 1
            update(playerCoord, aikaisempi)
            aikaisempi = elementAt(coord)
            update(coord, new Player(coord))
            playerCoord = coord
        }
      } 
    }
    maalissa
  }
  
  def convertCoords(c: Coords): Coords = {
    val x = if(c.x < 0) length + c.x else if(length - 1 < c.x) c.x - length else c.x
    val y = if(c.y < 0) map(0).length  + c.y else if(map(0).length - 1 < c.y) c.y - map(0).length else c.y
    new Coords(x,y)
  }
  
  def addTeleportCoords(c: Coords): Option[Coords] = {
    var result: Option[Coords] = None
    val half = length/2 -1 
    if(!(c.x == half && c.y == half)) {
    	//val x = if(c.x < half) half + half - c.x else c.x - half 
      //val y = if(c.y < half) half + half - c.y else c.y - half
      val x = length - 1 - c.x
      val y = length - 1 - c.y
      result = Some(convertCoords(new Coords(x, y)))
    } 
    result
  }
  
  def isBarrier(location: Coords): Boolean = this.labyrint(location.x)(location.y).isInstanceOf[Barrier]

  def update(location: Coords, newNode: Node) = {
    if(newNode.isInstanceOf[Player]) {
      playerCoord = location
    }
    this.labyrint(location.x)(location.y) = newNode
  }
  /*
  def filterLabyrint(sub: Int): Array[Array[Coords]] = {
    val v = sub/2
    println("playerCoord", playerCoord.x, playerCoord.y)
    val result = (0 until sub).toArray.map(y =>  (0 until sub).toArray.map(x => convertCoords(new Coords(playerCoord.x - (v - x), playerCoord.y - (v-y)))))
    result
  } */

  
  /**
   * Checks whether the grid contains the given x and y coordinates. 
   */
  private def contains(x: Int, y: Int): Boolean = x >= 0 && x < this.labyrint.length && y >= 0 && y < this.labyrint.length
   
 
  def length = labyrint.length
  
  /**
   * Determines whether the grid contains the given pair of coordinates.
   * For instance, a grid with a width and height of 5 will contain (0, 0) and (4, 4) but
   * not (-1, -1), (4, 5) or (5, 4). 
   */
  def contains(location: Coords): Boolean = this.contains(location.x, location.y) 

  def neighborElements(location: Coords) = {
    val results = Buffer[Node]()
    for (coords <- neighborCoords(location)) {
        results += this.elementAt(coords)
    }
    results
  }

  def neighborCoords(location: Coords): Buffer[Coords] = {
    val results = Buffer[Coords]()
    for (direction <- Direction.ValuesClockwise) {
        results += convertCoords(location.neighbor(direction))
    }
    val t = addTeleportCoords(location)
    if(t.isDefined) results += t.get;
     results
  }
  
  
  /**
   * Returns a collection of all the elements currently in the grid. 
   */
  def allElements: Iterable[Node] = 
    for {
      row <- 0 until this.length
      col <- 0 until this.length
      elem = this.elementAt(new Coords(col, row))
      if elem != null 
    } yield elem
  
  
  private def randomCoord(size: Int): Coords = {
    var rand = new Random
    var x = rand.nextInt(size)
    var y = rand.nextInt(size)
    new Coords(x,y)
  }
  
  
  def createMaze() = {
    var heap = new Heap
    playerCoord = randomCoord(length)
    def addNeighborsBarriers(location: Coords) = neighborElements(location).foreach((x: Node) => x match {
      case i: Barrier => {
          heap.add(i)
      }
      case _ =>
    })
    this.update(playerCoord, new Player(playerCoord))
    addNeighborsBarriers(playerCoord) 
    heap.printHeap
    var previous = playerCoord
    var nextCoords: Coords = null 
    while(heap.length > 0 ) {
        nextCoords = heap.pop.get
        if(this.isBarrier(nextCoords) && 1 == neighborElements(nextCoords).map((x:Node) => if(!x.isBarrier) 1 else 0).sum) {
        	//Käydään läpi "teleportit" -> jos floor niin tarkastetaan onko kyseinen vastapari floor, jos on niin updatetaan 
          val teleport = addTeleportCoords(nextCoords)
          if(teleport.isDefined && elementAt(teleport.get).isInstanceOf[Floor]) {
            this.update(nextCoords, new Teleport(nextCoords))
            this.update(teleport.get, new Teleport(teleport.get))
          } else {
        	  this.update(nextCoords, new Floor)
          }
          addNeighborsBarriers(nextCoords)
        }
    }
    aikaisempi = if(addTeleportCoords(playerCoord).isDefined) new Teleport(playerCoord) else new Floor()
    this.update(playerCoord, new Player(playerCoord))
    goalCoord = nextCoords
    this.update(nextCoords, new Goal(nextCoords))

   }
   
  def printLabyrint = {
      for(x<- 0 until this.length) {
       this.labyrint(x).foreach((x: Node) => print(x.toString() + " "))
       println(" ")
     }
  }
  
}


