package LabyrintManager

import scala.swing._
import java.awt.Graphics2D
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.event.MouseEvent

class GamePanel(game: Labyrint, scale: Int) extends GridPanel(game.length, game.length) {

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
    for (y <- 0 until rows) {
      for (x <- 0 until columns) {
        this.drawSquare(x, y, scale, icons, g)
      }
    }
  }
  
  
  private def drawSquare(x: Int, y: Int, size: Int, icons: Boolean, g: Graphics2D) = {
    if (!icons) {

      g.setColor(GridType.getColor(this.game.elementAt(new Coords(x,y))))
      g.fillRect(x * size, y * size, size, size)
    } else {

      if (this.game.elementAt(new Coords(x,y)).isInstanceOf[Player]) {
        g.drawImage(player, x * size, y * size, size, size, null)
        
      } else if (this.game.elementAt(new Coords(x,y)).isInstanceOf[Barrier]) {
       
        g.drawImage(barrier, x * size, y * size, size, size, null)
        
      } else if (this.game.elementAt(new Coords(x,y)).isInstanceOf[Teleport]) {
       
        g.drawImage(teleport, x * size, y * size, size, size, null)
        
      } else if (this.game.elementAt(new Coords(x,y)).isInstanceOf[Goal]) {
       
        g.drawImage(goal, x * size, y * size, size, size, null)
      } else if (this.game.elementAt(new Coords(x,y)).isInstanceOf[Floor]) {
      
        g.drawImage(floor, x * size, y * size, size, size, null)
      } else {
        
      g.setColor(GridType.getColor(this.game.elementAt(new Coords(x,y))))
      g.fillRect(x * size, y * size, size, size)
      }
    }

  }
}