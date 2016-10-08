package LabyrintManager
import scala.collection.mutable.Buffer

class Heap {

  private val heap = scala.collection.mutable.Buffer[Barrier]()

  private def lastIndex = heap.size - 1

  def length = heap.length
  
  def add(b: Barrier) = {
      heap.append(b)
      bubbleUp(lastIndex)
  }

  def swap(index1: Int, index2: Int) {
      val temp = heap(index1)
      heap.update(index1, heap(index2))
      heap.update(index2, temp)
  }

  def bubbleUp(currentIndex: Int) {
      if (currentIndex > 0) {
          val parentIndex = (currentIndex - 1) / 2
          if(heap(currentIndex)< heap(parentIndex)) {
              swap(currentIndex, parentIndex)
              bubbleUp(parentIndex)
          }
    }
  }

  def bubbleDown(currentIndex: Int) {
      getLowerChild(currentIndex) match {
          case Some((lowerChildIndex)) =>
              if (heap(currentIndex) > heap(lowerChildIndex)) {
                  swap(currentIndex, lowerChildIndex)
                  bubbleDown(lowerChildIndex)
            }
        case None =>
    }
  }
  
 def getLowerChild(index: Int): Option[Int] = {
    val leftChildIndex = 2 * index + 1
    val rightChildIndex =  2 * index+ 2
    if(!((rightChildIndex <= lastIndex) && (leftChildIndex <= lastIndex))) {
      None
    } else {
      if(heap(leftChildIndex) < heap(rightChildIndex)) {
        Some(leftChildIndex)
      } else {
        Some(rightChildIndex)
      }
    }
  }
  def pop: Option[Coords] = heap.size match {
    case 0 => None
    case _ =>
      val firstValue = heap(0)
        if (heap.size == 1) {
            heap.remove(0)
            return Some(firstValue.coords)
        }
        swap(0, lastIndex)
        heap.remove(lastIndex)
        bubbleDown(0)
        Some(firstValue.coords)
    }
  def printHeap = this.heap.foreach((x: Node)=> print(x.toString() +" "))
  }  