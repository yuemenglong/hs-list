import java.io.{File, FileInputStream, PrintWriter}
import java.nio.file.Paths

import io.github.yuemenglong.json.JSON
import io.github.yuemenglong.json.parse.JsonArr

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._
import scala.io.Source

/**
  * Created by Administrator on 2017/6/22.
  */


object Main {
  def main(args: Array[String]): Unit = {
//    findAll()
    replaceNumber()
  }

  def replaceNumber(): Unit ={
    val map = Map[String,String](
      "201730"->"201729"
    )
    ReplaceNo.replace("F:\\HoneySelect\\abdata\\list\\characustom\\fan_cos_01.unity3d", map)
  }

  def findDiff(): Unit = {
    val r0 = parseDir("D:/list/0/characustom")
    println(r0.length)
    val r1 = parseDir("D:/list/1/characustom")
    println(r1.length)


    val writer = new PrintWriter(new File(Paths.get("D:/list/0/characustom", "__diff.txt").toString))
    val s0: Set[String] = r0.map(_._3)(collection.breakOut)
    r1.foreach(r => {
      if (!s0.contains(r._3)) {
        println(r)
        writer.write(JSON.stringify(List[String](r._1, r._2, r._3, r._4)))
        writer.write("\n")
      }
    })
    writer.close()
  }

  def findAll(): Unit = {
    val dir = "F:\\HoneySelect\\abdata\\list\\characustom"
    val rows = parseDir(dir)
    println(rows.length)
    val writer = new PrintWriter(new File(Paths.get(dir, "__all.txt").toString))
    rows.sortBy(_._2).foreach(r => {
      println(r)
      writer.write(JSON.stringify(List[String](r._2, r._3, r._4, r._1)))
      writer.write("\n")
    })
    writer.close()
  }

  def findDup(): Unit = {
    val dir = "F:\\HoneySelect\\abdata\\list\\characustom"
    val rows = parseDir(dir)
    println(rows.length)
    val writer = new PrintWriter(new File(Paths.get(dir, "__dup.txt").toString))
    var last: (String, String, String, String) = ("", "", "", "")
    rows.sortBy(_._2).foreach(r => {
      if (last._2 == r._2 && last._3 != r._3) {
        println(last)
        println(r)
        println()
        writer.write(JSON.stringify(Array[String](last._1, last._2, last._3, last._4)))
        writer.write("\n")
        writer.write(JSON.stringify(Array[String](r._1, r._2, r._3, r._4)))
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
      ParseFile.parseFileSimple(file).map(p => {
        val row = p
        (file.getPath, row(0), row(2), row(4))
      })
    })
    val file = new File(Paths.get(dir, "list.txt").toString)
    val writer = new PrintWriter(file)
    rows.foreach(row => {
      writer.write(JSON.stringify(List[String](row._1, row._2, row._3, row._4)))
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
      file.listFiles().flatMap(listFiles).toList
    } else {
      List()
    }
  }
}
