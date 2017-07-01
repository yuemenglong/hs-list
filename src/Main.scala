import java.io.{File, FileInputStream, PrintWriter}
import java.nio.file.Paths

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._
import scala.io.Source
import scala.util.parsing.json.JSONArray

/**
  * Created by Administrator on 2017/6/22.
  */


object Main {
  def main(args: Array[String]): Unit = {
    ParseFile.parseFile(new File("F:\\HoneySelect\\abdata\\list\\characustom/00.unity3d")).foreach(row=>{
      row.foreach(item=>{
        println(item)
      })
    })
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
      ParseFile.parseFile(file).map(row => {
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
