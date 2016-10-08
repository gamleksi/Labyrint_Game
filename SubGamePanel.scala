package LabyrintManager

import scala.swing._
import java.awt.Graphics2D
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.event.MouseEvent

class SubGamePanel(game: Labyrint, scale: Int) extends GridPanel(game.length, game.length) {

  private var player: BufferedImage = null
  private var barrier: BufferedImage = null
  private var teleport: BufferedImage = null
  private var goal: BufferedImage = null
  private var floor: BufferedImage = null
  private var icons = true
  
  
  //yrittää avata kuvatiedostot annetuista poluista. Jos polkujen kuvia ei löydy, vaihtaa pelin grafiikat 
  
  try {
    player = ImageIO.read(new File("src/LabyrintManager/mario.gif"))
    teleport = ImageIO.read(new File("src/LabyrintManager/goombo.gif"))
    barrier = ImageIO.read(new File("src/LabyrintManager/seina.gif"))
    goal = ImageIO.read(new File("src/LabyrintManager/tatti.gif"))
    floor = ImageIO.read(new File("src/LabyrintManager/maa.gif"))
    
  } catch {
    case e: Exception => println("Pictures not found")
      icons = false
      e.printStackTrace()
  }
  
  def switchToIcons = {
    icons = true
  }

  def switchToColors = {
    icons = false
  }
  
  override def paintComponent(g: Graphics2D): Unit = {
    val subTable = game.filterLabyrint(game.length)
    for (i <- 0 until rows) {
      for (j <- 0 until columns) {
        println(subTable(i)(j))
        this.drawSquare(subTable(j)(i), i, j, scale, icons, g)
      }
    }
  }
  
  //Hienosäädä jos jää aikaa
  
  private def drawSquare(coord: Coords, x: Int, y: Int, size: Int, icons: Boolean, g: Graphics2D) = {

    if (!icons) {

      g.setColor(GridType.getColor(this.game.elementAt(coord)))
      g.fillRect(x * size, y * size, size, size)
    } else {

      if (this.game.elementAt(coord).isInstanceOf[Player]) {
        g.drawImage(player, x * size, y * size, size, size, null)
        
      } else if (this.game.elementAt(coord).isInstanceOf[Barrier]) {
       
        g.drawImage(barrier, x * size, y * size, size, size, null)
        
      } else if (this.game.elementAt(coord).isInstanceOf[Teleport]) {
       
        g.drawImage(teleport, x * size, y * size, size, size, null)
        
      } else if (this.game.elementAt(coord).isInstanceOf[Goal]) {
       
        g.drawImage(goal, x * size, y * size, size, size, null)
      } else if (this.game.elementAt(coord).isInstanceOf[Floor]) {
      
        g.drawImage(floor, x * size, y * size, size, size, null)
      } else {
        
      g.setColor(GridType.getColor(this.game.elementAt(coord)))
      g.fillRect(x * size, y * size, size, size)
      }
    }

  }
}