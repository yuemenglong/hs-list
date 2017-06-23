import java.io.{File, FileInputStream, PrintWriter}
import java.nio.file.Paths

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._
import scala.io.Source
import scala.util.parsing.json.JSONArray

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
    //    parseFile(new File("D:/list/0/characustom/00.unity3d")).foreach(row => {
    //      row.foreach(println)
    //    })
    findDiff()
  }

  def findDiff(): Unit = {
    val r0 = parseDir("D:/list/0/characustom")
    println(r0.length)
    val r1 = parseDir("D:/list/1/characustom")
    println(r1.length)


    val writer = new PrintWriter(new File(Paths.get("D:/list/0/characustom", "diff.txt").toString))
    val s0: Set[String] = r0.map(_._3)(collection.breakOut)
    r1.foreach(r => {
      if (!s0.contains(r._3)) {
        println(r)
        writer.write(JSONArray(List[String](r._1, r._2, r._3, r._4)).toString())
        writer.write("\n")
      }
    })
    writer.close()
  }

  def findDup(): Unit = {
    val dir = "D:/list/0/characustom"
    val rows = parseDir(dir)
    println(rows.length)
    val writer = new PrintWriter(new File(Paths.get(dir, "diff.txt").toString))
    var last: (String, String, String, String) = ("", "", "", "")
    rows.sortBy(_._2).foreach(r => {
      if (last._2 == r._2 && last._3 != r._3) {
        println(last)
        println(r)
        println()
        writer.write(JSONArray(List[String](last._1, last._2, last._3, last._4)).toString())
        writer.write("\n")
        writer.write(JSONArray(List[String](r._1, r._2, r._3, r._4)).toString())
        writer.write("\n")
        writer.write("\n")
      }
      last = r
    })
    writer.close()
  }

  def parseDir(dir: String): List[(String, String, String, String)] = {
    val rows = listFiles(dir).flatMap(file => {
      println(file)
      parseFile(file).map(row => {
        (file.getPath, row(0), row(2), row(4))
      })
    })
    val file = new File(Paths.get(dir, "list.txt").toString)
    val writer = new PrintWriter(file)
    rows.foreach(row => {
      writer.write(JSONArray(List[String](row._1, row._2, row._3, row._4)).toString())
      writer.write("\n")
    })
    writer.close()
    rows
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
      true
    } else {
      false
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
        val line = buffer.drop(start).take(end - start)
        val items = splitBy(line, 0x09)
        if (items.length > 1) {
          require(items.length > 10)
          rows += items.map(new String(_))
        }
        pos = end
      }
    }
    rows.toArray
  }

  def listFiles(path: String): List[File] = {
    listFiles(new File(path))
  }

  def listFiles(file: File): List[File] = {
    if (file.getName.endsWith(".unity3d")) {
      return List(file)
    }
    if (file.isDirectory) {
      file.listFiles().map(listFiles).flatten.toList
    } else {
      List()
    }
  }
}
