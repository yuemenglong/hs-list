import java.io.{File, FileInputStream}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

/**
  * Created by yml on 2017/7/1.
  */

/*
header: ca_f_leg_00
@@@
0x00 0x00
###0x09###0x09###0x0A
###0x09###0x09###0x0A
###0x09###0x09###0x0A00
 */
object ParseFile {
  val TAG = ".unity3d"

  def parseFile(file: File): Array[Array[(Int, Int, String)]] = {
    val is = new FileInputStream(file)
    val buffer = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
    var pos = 0
    val rows = new ArrayBuffer[Array[(Int, Int, String)]]
    breakable {
      while (true) {
        pos = buffer.indexOfSlice(TAG, pos)
        if (pos < 0) {
          break
        }
        val start = Math.max(buffer.lastIndexOf(0x0a, pos), buffer.lastIndexOf(0x00, pos)) + 1
        val end = (buffer.indexOf(0x0a, pos), buffer.indexOf(0x00, pos)) match {
          case (-1, -1) => buffer.length
          case (a, -1) => a
          case (-1, b) => b
          case (a, b) => Math.min(a, b)
        }
        val line = buffer.slice(start, start + end - start)
        val items = Kit.splitBy(line, 0x09)
        if (items.length > 1) {
          require(items.length > 10)
          rows += items.map(item=>{
            (start, end, new String(item))
          })
        }
        pos = end
      }
    }
    is.close()
    rows.toArray
  }

  def parseFileSimple(file: File): Array[Array[String]] ={
    parseFile(file).map(_.map(_._3))
  }
}
