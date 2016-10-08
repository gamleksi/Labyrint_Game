package LabyrintManager
import scala.swing._
import javax.swing.border._
import java.awt.Color
import scala.swing._
import scala.swing.event._
import java.awt.event._

object LabyrintUi extends SimpleSwingApplication {
  
  /**MUUTTUJIA
   * */
  val labyrintManager = new LabyrintManager
  var labyrintMap: Labyrint = null
  val sizeX = 1200
  val sizeY = 900
  val sivureunaX = 200
  val sivuraunaY = 1000
  val labyrinttiX = 900
  val labyrinttiY = 900
  val säännöt = "Huhhuh tulee kiire"
  
  val scaleGamePanel = 15
  val scaleSubGamePanel = 30
/**APUFUNKTIOITA
* */  
 private def openFile = {
    val chooser = new FileChooser
    if(chooser.showOpenDialog(null) == FileChooser.Result.Approve) {
      //Tarkistus onko kyseinen file labyrintti format. Ratkaisu: tähän jokin tarkistus, että on txt format ja loadLabyrint metodi tarkistaa ensimäisen rivin "tunnuksen"
      labyrintManager.loadLabyrint(chooser.selectedFile.toString)
    }
  }
  
  private def quitGame = {
    val escape = Dialog.showConfirmation(new BorderPanel, "Haluatko lopettaa?", optionType = Dialog.Options.YesNo, title = "Labyrintti")
    if (escape == Dialog.Result.Yes) sys.exit
  }
  
  
/**KÄYTTÖLIITTYMIÄ
* */  
  

  def gameFrame: BorderPanel = new BorderPanel {
        var mazeCreated = false
        var subMap = false
        val tilasto = new Label("Tähän tulee aikaisempien yritysten tilastoja")  
        val ohjeet = new Label(säännöt)
        
        val labyrinttiUi = new GamePanel(labyrintMap, scaleGamePanel) {
          this.preferredSize = new Dimension(labyrinttiX, labyrinttiY)
        }
        
        var subLabyrinttiUi: SubGamePanel = new SubGamePanel(labyrintMap, scaleSubGamePanel) {
          this.preferredSize = new Dimension(labyrinttiX, labyrinttiY)
        }
        
        val sivureuna: BoxPanel = new BoxPanel(Orientation.Vertical) {
        	contents += ohjeet
          contents += new Separator
          contents +=  new Button("Generoi labyrintti") {
            listenTo(mouse.clicks)
            reactions += {
            case ButtonClicked(_) => if(!mazeCreated) {
              this.text = "Starttaa"
              mazeCreated = true
              labyrintMap.createMaze()
              labyrinttiUi.repaint()
            } else if(!subMap) {
              this.text = "Vaihdo kuvakulmaa"
              subMap = true
              subLabyrinttiUi.repaint()
              add(subLabyrinttiUi, BorderPanel.Position.Center)
            } else {
              subMap = false
              this.text = "Vaihdo kuvakulmaa"
              labyrinttiUi.repaint()
              add(labyrinttiUi, BorderPanel.Position.Center)
            }
            case _ => 
          }
          }
         contents += new Separator
         contents += new Button("Palaa takaisin") {
            listenTo(mouse.clicks)
            reactions += {
              case ButtonClicked(_) => top.contents = menu
              case _ => 
            }     
         }  
        
        contents += new Separator
        contents += tilasto
        this.preferredSize = new Dimension(sivureunaX, sivuraunaY)
    }
    
    add(labyrinttiUi, BorderPanel.Position.Center)
    add(sivureuna, BorderPanel.Position.West)
/**REAKTIOT
 * */        
        
        listenTo(keys)
        reactions += {
        case scala.swing.event.KeyPressed(_,b,_,_) => {
        	if(mazeCreated) { 
          b match {
                case Key.Up => {
                  if(labyrintMap.movePlayer(North)) {
                	  println("Onneksi OLKOON");
                  }
                  if(subMap) {
                    subLabyrinttiUi.repaint()
                  } else labyrinttiUi.repaint
                }
                case Key.Down => {
                  if(labyrintMap.movePlayer(South)) {
                    
                	  println("Onneksi OLKOON");
                  }
                  if(subMap) {
                    subLabyrinttiUi.repaint()
                  } else labyrinttiUi.repaint
                }
                case Key.Left => {
                  if(labyrintMap.movePlayer(West)) {
                	  println("Onneksi OLKOON");
                  }
                  if(subMap) {
                    subLabyrinttiUi.repaint()
                  } else labyrinttiUi.repaint
                }
                case Key.Right => {
                  if(labyrintMap.movePlayer(East)) {
                	  println("Onneksi OLKOON");
                  }
                  if(subMap) {
                    subLabyrinttiUi.repaint()
                  } else labyrinttiUi.repaint
                }
                case Key.Shift => {
                  if(labyrintMap.movePlayer(Lift)) {
                	  println("Onneksi OLKOON");
                  }
                  if(subMap) {
                    subLabyrinttiUi.repaint()
                  } else labyrinttiUi.repaint();
                }
                case Key.Space => {
                  if(subMap) {
                    subMap = false
                    add(labyrinttiUi, BorderPanel.Position.Center)
                  } else {
                    subMap = true                    
                    add(subLabyrinttiUi, BorderPanel.Position.Center)
                  }
                }
                case _ =>
              }
              }
            }
        case _ => requestFocus
          }      
        
    preferredSize = new Dimension(sizeX, sizeY)
    requestFocus
  }
  
  
 val newGameUi: BoxPanel = new BoxPanel(Orientation.Vertical) {
    val textField = new TextField(10) {
      this.preferredSize = new Dimension()
    }
    
    contents += new Label("Aseta kentälle nimi (max 10 merkkiä, HUOM samanniminen kenttä poistetaan): ")
    def helper(size: Int) =  {
      val t = textField.text.filter { x => " " != x }
            if(t.size < 1) {
             varoitusMerkintä.text = "Kentän nimi tulee määritellä"
             varoitusMerkintä.foreground = Color.red
            } else {
              val fileName = t + ".txt"
              labyrintManager.createNewLabyrintFile(fileName , size)
              labyrintManager.loadLabyrint(fileName)
              if(labyrintManager.getLabyrint.isDefined) {
                labyrintMap = labyrintManager.getLabyrint.get
                top.contents = gameFrame
              } else {
                println("jokin meni pieleen")
                top.contents = menu
              }
            }

    }
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += textField
    }
    contents += new Button("Easy") {
      listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(_) => helper(50); //sivunPituus = 50
          case _ => 
        }
    }
    /**Koko on 50*/
    contents += new Button("Normal") {
      listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(_) => helper(70); //sivunPituus = 70
          case _ => 
        }
    }
    /** Koko on 70*/
    contents += new Button("Hard") {
      listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(_) => helper(100);// sivunPituus = 100
          case _ => 
        }
    }
    /** Koko on 100*/
    contents += new Button("Return") {
        reactions += {
          case ButtonClicked(e) => top.contents = menu
          case e => 
        }
    }
    var varoitusMerkintä = new Label() {
      
    }
    contents += varoitusMerkintä
         preferredSize = new Dimension(sizeX, sizeY)

  } 

   val menu = new BorderPanel { 
      val boxMenu = new BoxPanel(Orientation.Vertical) {
      contents += new Label("Tervetuloa!") {
        this.background = Color.green
        this.font = new Font("Comic Sans", 0, 36)
        this.foreground = Color.blue
        this.opaque = true
      }
      contents += new Separator
      contents += new Button("Uusi peli") {
        this.background = Color.green
        this.font = new Font("Comic Sans", 0, 36)
        this.foreground = Color.pink
        listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(e) => {
             top.contents = newGameUi
          }
          case e => 
        }
        opaque = true
      }
      contents += new Separator
      contents += new Button("Lataa peli"){
        this.background = Color.green
        this.font = new Font("Comic Sans", 0, 36)
        this.foreground = Color.pink
        listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(e) => openFile; top.contents = loadGameUi
          case e => 
        }
        opaque = true
      }
      contents += new Separator
      contents += new Button("Lopeta peli") {
        this.background = Color.green
        this.font = new Font("Comic Sans", 0, 36)
        this.foreground = Color.pink
        listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(e) => quitGame
          case e => 
        }
        opaque = true
      }
      border = Swing.EmptyBorder(30, 30, 10, 30)
      background = Color.blue
      opaque = true
    }
     preferredSize = new Dimension(sizeX, sizeY)
     add(boxMenu, BorderPanel.Position.Center)
  }
   
  val loadGameUi: BoxPanel = new BoxPanel(Orientation.Vertical) {
    if(labyrintManager.getLabyrint.isDefined) {
      contents += new Label("Yo")
      //Tähän lisätilanne, jos löytyy lataus.
    } else {
      contents += new Label("Your file is not readable.")
      contents += new Button("Open different file.")  
      contents += new Button("Return") {
      listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(e) => top.contents = menu
          case e => 
        }
      }
    }
    this.preferredSize = new Dimension(sizeX,sizeY)
   } 
   
   
   
 

 val top: MainFrame =  new MainFrame {
    title = "Labyrintti"
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Toka") {

        })
        contents += new Separator
        contents += new MenuItem(Action("Open") {
        })
        contents += new Separator
        contents += new MenuItem(Action("K") {
        })
        contents += new Separator
          contents += new MenuItem(Action("Eka") {

        })
        contents += new Separator
        contents += new MenuItem(Action("Exit") {
          quitGame
        })        
      }
    }
    contents = menu 
    size = new Dimension(sizeX, sizeY)
  }   
   
  
  def go() 
   {
    top.centerOnScreen()
    top.visible = true
  }
}