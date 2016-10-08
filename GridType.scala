package LabyrintManager

import java.awt.Color

object GridType extends Enumeration {
 
//	type GridType = Value
//   val PLAYER, WALL, OBSTACLE, GOAL, EMPTY = Value
   
   def getColor(value: Node): Color = value match {
     case x: Floor => new Color(255,0,0)
     case x: Barrier => new Color(0,0,0)
     case x: Player => new Color(0,255,255)
     case x: Goal => new Color(255,255,255)
     case x: Teleport => new Color(0,255,100)
     case _ => new Color(0,0,0)
    }
   
  
}