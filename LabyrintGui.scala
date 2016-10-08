package LabyrintManager
import scala.swing._
import javax.swing.border._
import java.awt.Color
import scala.swing._
import scala.swing.event._
import java.awt.event._

//import javax.swing.SpringLayout.Constraints
//import scala.swing.GridBagPanel.Constraints
object LabyrintGui extends SimpleSwingApplication {
  
  val m = 75
  val sub = 25
  val preferSize = 700
  var sivunPituus = 0
  val labyrintManager = new LabyrintManager
  
  //val buttonToCoords = new scala.collection.mutable.HashMap[Button, Coords]()
  val coordsToButton = new scala.collection.mutable.HashMap[Coords, Button]()
  // Tarkistus ennen pelin aloittamista, että jokainen osa löytyy pelistä.
  var player: Option[Coords] = None
  
  var goal: Option[Coords] = None
  
  
  //Pienentää labyrintin koon niin että keskimmäinen koordinaatti on playerin. Ei toimi oikein tällä hetkellä.
  private def filterLabyrint(coord: Coords): Array[Array[Coords]] = {
    //m tulee olla pariton mieti siirtoa muuhun luokkaan?
    val v = sub/2
    println("filtteröinti tehty")
    val result = (0 until sub).toArray.map(y =>  (0 until sub).toArray.map(x => this.labyrintManager.getLabyrint.get.convertCoords(new Coords(coord.x - (v - x), coord.y - (v-y)))))
    //result.foreach { _.foreach { x => println(x) } }
    result
  } 
  //Tällä hetkellä purkkaratkaisu.
  private def isBarrier(x: Button) = {
    x.background == Color.green
  }
  
  private def convertToPlayer(f: Button) = {
    f.background = Color.black
    f.text = "P"
    f.border = new MatteBorder(1, 1, 1, 1, Color.red)   
  }
  
  private def convertToFloor(f: Button) = {
    f.background = Color.gray
    f.text = " "
    f.border = new MatteBorder(0, 0, 0, 0, Color.white)
  }

  import scala.swing.event.Key._
// Koko labyrintti graafisesti näkyvissä
  var length = 0 
 def ui = {
    if(labyrintManager.getLabyrint.isDefined) {
        val mapLength = labyrintManager.getLabyrint.get.length
        length = mapLength
        val maze = new GridPanel(mapLength,mapLength) {
      for(j<-0 until mapLength) {
        for(i<-0 until mapLength) {
          //Tähän parempi graaffinen muotoilu
            val button = new Button()
            button.background = Color.green
            button.foreground = Color.black
            button.border = new MatteBorder(1, 1, 1, 1, Color.green)
            button.opaque = true
            contents += button
            coordsToButton(new  Coords(i,j)) = button
            this.preferredSize = new Dimension(700, 900)
          }
        } 
      }  
    val ui = new BorderPanel {
        val tilasto = new Label("Tähän tulee aikaisempien yritysten tilastoja")  
        val ohjeet = new Label("Tähän tulee pelistä lyhyt ohje")
        val sivureuna = new BoxPanel(Orientation.Vertical) {
        contents += ohjeet
        contents += new Separator
        contents += new BoxPanel(Orientation.Horizontal) {
          contents +=  new Button("Luo kenttä") {
          listenTo(mouse.clicks)
          var mazeLuotu = false
          reactions += {
            case ButtonClicked(_) => if(!mazeLuotu) {
            	println("Onnistui", createMaze);
            	this.text_=("Siirry liikkumistilaan ja aloita peli")
              mazeLuotu = true
            } else {
               if(player.isDefined) {
                  top.contents = newUi
               } else {
                 println("Playeria ei löydy")
                 top.contents = menu
               }
            }
            case _ => 
          }
          }
       contents += new Button("Palaa takaisin") {
        listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(_) => top.contents = menu
          case _ => 
        }     
       }  
      }
      contents += new Separator
      contents += tilasto
      this.preferredSize = new Dimension(200, 900)
    }
    add(sivureuna, BorderPanel.Position.West)
    add(maze, BorderPanel.Position.Center)
   }
    top.contents = ui
   } else {
     println("jotain meni pieleen")
  }  
 }
 /*  
  val ui: GridPanel = new GridPanel(m, m) {
    preferredSize = new Dimension(preferSize, preferSize)
    //Vanha ratkaisu, ei ota huomioon teleporttia jätetään ainakin hetkeksi tähän
    
    def moveHelper(d: Direction) = {
      val c = labyrintManager.getLabyrint.get.convertCoords(player.get.neighbor(d))
      //println(c)
      val n = coordsToButton(c)
      if(!isBarrier(n)) {
        convertToPlayer(n)
        convertToFloor(coordsToButton(player.get))
        player = Some(c)
      }
    }
    
    listenTo(keys, mouse.clicks, mouse.moves)
        reactions += {
          case scala.swing.event.KeyPressed(_,b,_,_) => {
            b match {
                case Up => moveHelper(North)
                case Down => moveHelper(South)
                case Left => moveHelper(West)
                case Right => moveHelper(East)
                case Space => {
                 deafTo(keys, mouse.clicks, mouse.moves); 
                 val uiScreen = newUi(player.get); 
                   top.contents = uiScreen; 
                   uiScreen.requestFocus(); 
                }  
                case e => requestFocus
            }
          }
          case e => {
            requestFocus
          }
      } 
    requestFocus
  }
*/    
  //Luod ui screenin.

  /*  Vanha ratkaisu
    def generateBorders = {
      var sgi = 0
      var sgiRow = 0
      var sgiStepper = labyrintManager.getLabyrint.get.length
      val map = labyrintManager.getLabyrint.get
      for(i<-0 until m) {
        sgiRow += 1
        for(j<-0 until m) {
          val button = ui.contents(sgi).asInstanceOf[Button]
          button.background = Color.green
          button.foreground = Color.black
          button.border = new MatteBorder(1, 1, 1, 1, Color.green)
          sgi += sgiStepper
          coordsToButton(new  Coords(i,j)) = button
        }
         //coordsToButton.keys.foreach { x => println(x) }
         sgi = sgiRow
        }
       top.contents = ui
       ui.requestFocus
        
      }  
*/  
  

  //Rakennetaan labyrintti ruudulle
  private def createMaze = {
      if(!labyrintManager.constructLabyrint) {
       println("Jotain meni väärin")
      } else {
      var sgi = 0
      val map = labyrintManager.getLabyrint.get
      val mapIt = map.allElements.iterator
      for(j <- 0 until map.length) {
        for(i<- 0 until map.length) {
          val nextElement = mapIt.next()
          val button = coordsToButton(new Coords(i,j)).asInstanceOf[Button]
          nextElement match {
          case e :Floor => {
            button.background = Color.white
            button.text = " "
            button.border = new MatteBorder(0, 0, 0, 0, Color.white)
          }
          case e: Player => {
            println("Player", nextElement.asInstanceOf[Player].coords)
            player = Some(nextElement.asInstanceOf[Player].coords)
            button.background = Color.black
            button.text = "P"
            button.border = new MatteBorder(1, 1, 1, 1, Color.red)
          }
          case e: Goal => {
            goal = Some(nextElement.asInstanceOf[Goal].coords)
            button.background = Color.yellow
            button.text = "G"
            button.border = new MatteBorder(1, 1, 1, 1, Color.black)            
          }
          case e: Teleport => {
            button.background = Color.blue
            button.border = new MatteBorder(1, 1, 1, 1, Color.blue)                        
          }
          case _ => 
          }
        }
      }
    } 
  }
  /*
    add(labyrintUi, BorderPanel.Position.Center)
    this.preferredSize = new Dimension(preferSize,preferSize)
   }
   * */
  
  // Muodostaa ruudulle pienemmän osan labyrintistä ja mahdollistaa liikkumisen
  def newUi: BorderPanel = new BorderPanel {
        val tilasto = new Label("Tähän tulee aikaisempien yritysten tilastoja")  
        val ohjeet = new Label("Tähän tulee oman suorituksen pistetilanne")
        val sivureuna = new BoxPanel(Orientation.Vertical) {
        contents += ohjeet
        contents += new Separator
        contents += new BoxPanel(Orientation.Horizontal) {
          contents +=  new Button("Starttaa peli") {
          listenTo(mouse.clicks, keys)
          reactions += {
            //case KeyPressed(_,_,_,_) => println("nappulouta"); add(centerUi, BorderPanel.Position.Center)
            case scala.swing.event.KeyPressed(_,b,_,_) => println("keys", b)
            case ButtonClicked(e) => {
            	println("Mitä hittoa? ", e); 
              add(centerUi, BorderPanel.Position.Center)
            }
            case e => println("jos tämä toimisi", e)
          }
          
          }
       contents += new Button("Luovuta") {  //ShortestPath niin, että ensin koko kenttä avataan näkyviin.
        listenTo(mouse.clicks, keys)
        reactions += {
          case ButtonClicked(_) => top.contents = menu
          case _ => 
        }     
       }  
      }
      contents += new Separator
      preferredSize = new Dimension(200, 900)
    }


     var centerUi = new GridPanel(sub, sub) {
       val table = filterLabyrint(player.get)
       for(row <- table){
          for(coord <- row) {
            contents += coordsToButton(coord)
          }  
        }
       this.preferredSize = new Dimension(700, 900)
      }
        def moveHelper(d: Coords)= {
          val c = labyrintManager.getLabyrint.get.convertCoords(d)
          val n = coordsToButton(c)
          var result = false
          if(!isBarrier(n)) {
            result = true
            convertToPlayer(coordsToButton(c))
            convertToFloor(coordsToButton(player.get))
            player = Some(c)
            centerUi = new GridPanel(sub, sub) {
             val table = filterLabyrint(c)
             for(row <- table){
              for(coord <- row) {
            	  contents += coordsToButton(coord)
             }    
            }
             this.preferredSize = new Dimension(700, 900)
           }
          }

          result
         }
        
        listenTo(keys, mouse.clicks)
        reactions += {
        case scala.swing.event.KeyPressed(_,b,_,_) => {
          b match {
                case Up => println("toimiiko ylös"); if(moveHelper(player.get.neighbor(North))) {
                  println(centerUi.contents.length)                  
                  //add(sivuReunanRakennus, BorderPanel.Position.West)                  
                  add(centerUi, BorderPanel.Position.Center)
                }
                case Down => if(moveHelper(player.get.neighbor(South))) {
                  println(centerUi.contents.length)                  
                  //add(sivuReunanRakennus, BorderPanel.Position.West)                  
                  add(centerUi, BorderPanel.Position.Center)
                }
                case Left => if(moveHelper(player.get.neighbor(West))) {
                  println(centerUi.contents.length)
                  //add(sivuReunanRakennus, BorderPanel.Position.West)                  
                  add(centerUi, BorderPanel.Position.Center)
                }
                case Right => if(moveHelper(player.get.neighbor(East))) {
                  println(centerUi.contents.length)
                  add(centerUi, BorderPanel.Position.Center)
                                    
                }
                case Shift => {
                  val teleportCoord = labyrintManager.getLabyrint.get.addTeleportCoords(player.get)
                  if(teleportCoord.isDefined && moveHelper(teleportCoord.get)) {
                    add(centerUi, BorderPanel.Position.Center)
                  }
                }
                case Space => {
                  top.contents = mapFrame
                 }
                case e => 
            }
          }


          case e => {
            requestFocus
          }
         }
        add(centerUi, BorderPanel.Position.Center)
        add(sivureuna, BorderPanel.Position.West)
        requestFocus
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
  }
  
  
  def mapFrame = new BorderPanel {
      //Tee ehtotarkastelu, että on pituus riittävä
     var map = new GridPanel(sivunPituus, sivunPituus) {
       for(j <- 0 until sivunPituus) {
         for(i <- 0 until sivunPituus) {
           contents += coordsToButton(new Coords(i, j))   
         }
       }
      this.preferredSize = new Dimension(800, 800)
     }
      
      val buttons = new BoxPanel(Orientation.Vertical) {
       contents += new Button("Starttaa peli") {
         listenTo(mouse.clicks)
         reactions += {
           case ButtonClicked(_) => add(map, BorderPanel.Position.Center)
         }         
       }
       contents += new Button("Palaa takaisin") {
         listenTo(mouse.clicks)
         reactions += {
           case ButtonClicked(_) => top.contents = newUi
         }         
       }
      this.preferredSize = new Dimension(900, 100)
     }

      add(map,  BorderPanel.Position.Center)
       add(buttons, BorderPanel.Position.North)

    def moveHelper(d: Coords) = {
      val c = labyrintManager.getLabyrint.get.convertCoords(d)
      val n = coordsToButton(c)
      if(!isBarrier(n)) {
        convertToPlayer(n)
        convertToFloor(coordsToButton(player.get))
        player = Some(c)
      }
    }
    focusable = true
    listenTo(keys)
        reactions += {
          case scala.swing.event.KeyPressed(_,b,_,_) => {
            b match {
                case Up => moveHelper(player.get.neighbor(North))
                case Down => moveHelper(player.get.neighbor(South))
                case Left => moveHelper(player.get.neighbor(West))
                case Right => moveHelper(player.get.neighbor(East))
                case Shift => {
                  val teleportCoord = labyrintManager.getLabyrint.get.addTeleportCoords(player.get)
                  if(teleportCoord.isDefined) moveHelper(teleportCoord.get)
                  }
                case Space => {
                  newUi.requestFocus()
                 top.contents = newUi
                }  
                case e => println("toimisiko mainframen keys", e)
            }
          }
          case e => {
            //println("toimisiko mainframe muu", e)
            requestFocus
          }
      } 
      requestFocus
    }
  
    val newGameUi: BoxPanel = new BoxPanel(Orientation.Vertical) {
	  val textField = new TextField(10) {
      
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
              ui
            }

    }
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += textField
    }
    contents += new Button("Easy") {
      listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(_) => helper(50); sivunPituus = 50
          case _ => 
        }
    }
    /**Koko on 50*/
    contents += new Button("Normal") {
      listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(_) => helper(70); sivunPituus = 70
          case _ => 
        }
    }
    /** Koko on 75*/
    contents += new Button("Hard") {
      listenTo(mouse.clicks)
        reactions += {
          case ButtonClicked(_) => helper(100); sivunPituus = 100
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
         preferredSize = new Dimension(1800, 1000)

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
     preferredSize = new Dimension(1800, 1000)
     add(boxMenu, BorderPanel.Position.Center)
  }
  private def newBorderPanelWindow(component: Component): BorderPanel = new BorderPanel {
    this.opaque = true
    this.background = Color.blue
    add(component, BorderPanel.Position.Center)
    //this.preferredSize = new Dimension(100, 100)
  }
  
  private def openFile = {
    val chooser = new FileChooser
    if(chooser.showOpenDialog(null) == FileChooser.Result.Approve) {
      //Tarkistus onko kyseinen file labyrintti format. Ratkaisu: tähän jokin tarkistus, että on txt format ja loadLabyrint metodi tarkistaa ensimäisen rivin "tunnuksen"
      labyrintManager.loadLabyrint(chooser.selectedFile.toString)
    }
  }
  
  
  val top: MainFrame =  new MainFrame {
    title = "Labyrintti"
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Toka") {
       //   execute
          //ui.updateGrid
          //ui.requestFocus()
        })
        contents += new Separator
        contents += new MenuItem(Action("Open") {
          openFile
        })
        contents += new Separator
        contents += new MenuItem(Action("K") {
        })
        contents += new Separator
          contents += new MenuItem(Action("Eka") {
          labyrintManager.createNewLabyrintFile("te1sti2.txt", m)
          labyrintManager.loadLabyrint("testi2.txt")
        })
        contents += new Separator
        contents += new MenuItem(Action("Exit") {
          quitGame
        })        
      }
    }
    contents = menu 
    size = new Dimension(1800, 1000)
  }
  
  private def quitGame = {
    val escape = Dialog.showConfirmation(new BorderPanel, "Haluatko lopettaa?", optionType = Dialog.Options.YesNo, title = "Labyrintti")
    if (escape == Dialog.Result.Yes) sys.exit
  }
  
  def go() 
   {
    top.centerOnScreen()
    top.visible = true
  }
  

}