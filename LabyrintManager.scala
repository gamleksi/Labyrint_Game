package LabyrintManager

import scala.util.Random
import java.io.PrintWriter
import scala.io.Source
import java.io._
import LabyrintManager.GridType._
import scala.swing._

//extends GridPanel(m, m) 
class LabyrintManager {

  private var labyrint: Option[Labyrint] = None

  def labyrintHasBeenCreated = labyrint.isDefined
  
  def getLabyrint = labyrint

  
  def createNewLabyrintFile(fileName: String, high: Int) = {
    val randomGenerator = new Random
    val file = new PrintWriter(fileName)
    println("Tiedoston luonti aloitetaan")
    try {
      file.println(high)
      for(n <- 1 to high) {
        for(i <- 1 to high) {  // tämä width jos halutaan muunkin muotoisia
          file.print(randomGenerator.nextInt(100))
          file.print(" ")
        }
        file.print('\n')
      }
      println("Tiedosto luoto")
    } finally {
      println("Tiedosto suljettu")
      file.close()
    }
  }
  
  def loadLabyrint(fileName: String)= {
    try {
      val file = Source.fromFile(fileName)  //TÄHÄN VIRHE TARKASTELU
      val lines = file.getLines()
      val size = lines.next().toInt
      val map = Array.ofDim[Node](size, size)
      var x = 0
      for(line<-lines){
        var row = line.split(" ")
        var y = 0
        for(value<- row){
          map(x)(y) = new Barrier(value.toInt, new Coords(x,y)) 
          y+=1
        }
        x += 1
     }
     labyrint = Some(new Labyrint(map))
     } catch {
       case e:IOException =>
       println( "Lukeminen päättyi virheeseen" )
     }
  }
  
  def constructLabyrint = {
    labyrint match {
      case None => println("Lataa ensin labyrint"); false
      case _ => labyrint.get.createMaze; true
    }
  }


}