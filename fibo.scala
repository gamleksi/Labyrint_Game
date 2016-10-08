package LabyrintManager

import scala.swing._
import scala.swing.event._
import scala.collection.mutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SlowFibonacci {

    // Stupidest and slowest way possible to calculate fibonacci
    def calc (n: Int):Int =
       if (n == 0 || n == 1)
           n
       else
           calc(n - 1) + calc(n - 2)
}

object GUIThreadExample extends SimpleSwingApplication {

    val minFib   = 1
    val maxFib   = 50
    val startFib = 15

    def makeFibonacciCounter(fibname: String) = new Slider{
        orientation = Orientation.Horizontal
        min   = minFib
        max   = maxFib
        value = startFib
        majorTickSpacing = 10
        paintTicks = true

        minimumSize = new Dimension(200, 300)

        val labelTable = HashMap[Int, Label]()
        labelTable += minFib    -> new Label(fibname)
        labelTable += maxFib/2  -> new Label("Fast")
        labelTable += 50        -> new Label("Quite long")
        labelTable += maxFib    -> new Label("Forever")

        labels = labelTable
        paintLabels = true
    }

    val eventThreadCounter = makeFibonacciCounter("Event thread")
    val otherThreadCounter = makeFibonacciCounter("Other thread")


    val numLabel = new Label(startFib.toString) {
        minimumSize = new Dimension(200,100)
    }

    def top = new MainFrame {
        contents = new BoxPanel(Orientation.Vertical) {
            contents += eventThreadCounter
            contents += otherThreadCounter
            contents += numLabel
        }

        size = new Dimension(700,200)
    }

    listenTo(eventThreadCounter)
    listenTo(otherThreadCounter)

    this.reactions += {
        case ValueChanged(source) =>
            val slider = source.asInstanceOf[Slider]

            if (!slider.adjusting) { // Calculate after the user stops moving the slider, not before

                slider.enabled = false // disable the slider (should go gray and unusable)

                if (slider == eventThreadCounter) {
                   // Bad Idea - heavy calculation in the event dispatch thread

                   val result = SlowFibonacci.calc(slider.value)
                   numLabel.text = result.toString
                   slider.enabled = true
                }
                else {
                   // Better idea - Heavy calculation in a Future

                   scala.concurrent.Future {
                      SlowFibonacci.calc(slider.value)
                   }.onSuccess {case result =>
                       // Future runs in a different thread, so update the GUI
                       // using the method Swing.onEDT which executes it's contents
                       // in the event dispatch thread

                       Swing.onEDT {
                           numLabel.text = result.toString
                           slider.enabled = true
                       }
                   }
              }
         }
    }
}