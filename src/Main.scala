import java.io.{File, FileInputStream}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._
import scala.io.Source

/**
  * Created by Administrator on 2017/6/22.
  */

/*
header: ca_f_leg_00
@@@
0x00 0x00
###0x09###0x09###0x0A
###0x09###0x09###0x0A
###0x09###0x09###0x0A00
 */
object Main {
  def main(args: Array[String]): Unit = {
    //    val rows = scan("D:/list/characustom").map(file => {
    //      println(file)
    //      parseFile(file)
    //    }).flatten
    //    rows.foreach(items => {
    //      println(items(0), items(2))
    //    })
    //    println(rows.length)
    parseFile(new File("D:\\list\\characustom\\08.unity3d"))
  }

  def splitBy(buffer: Array[Byte], flag: Byte): Array[Array[Byte]] = {
    val pos = buffer.indexOf(flag)
    pos match {
      case -1 => Array(buffer)
      case _ => {
        val spl = buffer.splitAt(pos)
        Array(spl._1) ++ splitBy(spl._2.drop(1), flag)
      }
    }
  }

  def splitBySlice(buffer: Array[Byte], flag: Seq[Byte]): Array[Array[Byte]] = {
    val pos = buffer.indexOfSlice(flag)
    pos match {
      case -1 => Array(buffer)
      case _ => {
        val spl = buffer.splitAt(pos)
        Array(spl._1) ++ splitBySlice(spl._2.drop(flag.length), flag)
      }
    }
  }

  def printBinary(buffer: Array[Byte]): Unit = {
    buffer.foreach(b => {
      printAble(b) match {
        case true => print("%c".format(b))
        case false => print("%02X".format(b))
      }
    })
    println()
  }

  def printAble(b: Byte): Boolean = {
    if ('a' <= b && b <= 'z' || 'A' <= b && b <= 'Z' || '0' <= b && b <= '9'
      || "_.".indexOf(b) >= 0) {
      return true
    } else {
      return false
    }
  }

  def parseFile(file: File): Array[Array[String]] = {
    val is = new FileInputStream(file)
    val buffer = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
    val tag = ".unity3d"
    var pos = 0
    val rows = new ArrayBuffer[Array[String]]
    breakable {
      while (true) {
        pos = buffer.indexOfSlice(tag, pos)
        if (pos < 0) {
          break
        }
        val start = Math.max(buffer.lastIndexOf(0x0a, pos), buffer.lastIndexOf(0x00, pos)) + 1
        //        val end = Math.min(buffer.indexOf(0x0a, pos), buffer.indexOf(0x00, pos))
        val end = (buffer.indexOf(0x0a, pos), buffer.indexOf(0x00, pos)) match {
          case (-1, -1) => buffer.length
          case (a, -1) => a
          case (-1, b) => b
          case (a, b) => Math.min(a, b)
        }
        println("%X, %X, %X".format(start, end, pos))
        val line = buffer.drop(start).take(end - start)
        val items = splitBy(line, 0x09)
        if (items.length > 1) {
          require(items.length > 10)
          rows += items.map(new String(_))
        }
        pos = end
      }
    }
    return rows.toArray
  }

  def scan(path: String): List[File] = {
    scan(new File(path))
  }

  def scan(file: File): List[File] = {
    if (file.getName.endsWith(".unity3d")) {
      return List(file)
    }
    if (file.isDirectory) {
      file.listFiles().map(scan).flatten.toList
    } else {
      List()
    }
  }
}
