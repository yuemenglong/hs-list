package bean

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import scala.collection.mutable.ArrayBuffer
import java.io.{File, PrintWriter}
import java.nio.file.Paths

import io.github.yuemenglong.json.JSON


/**
  * Created by Administrator on 2017/6/22.
  */

case class Parsed(list: String, id: String, name: String, abdata: String)

//(file.getPath, row(0), row(2), row(4)) // list id name data

object Main {
  def main(args: Array[String]): Unit = {
    //    copyNonExist()
    findDiff()
    //    findDup()
//        findAll()
//        replaceNumber()
//        copyListAndData()
  }

  def copyListAndData(): Unit = {
    val p0 = "F:\\HoneySelect"
    val p1 = "G:\\HS\\整合\\【糖糖游戏-甜心12.0】\\HoneySelect4K.12.0\\HoneySelect4K"
    //    val p1 = "G:\\HS\\整合\\hsv12\\HoneySelect1.1O"
//    CopyListAndData.go(p1, p0, "ash_set_01.unity3d")
  }

  def replaceNumber(): Unit = {
    val map = Map[String, String](
      "206479" -> "206481",
    )
    ReplaceNo.replace("F:\\HoneySelect\\abdata\\list\\characustom\\race_01_list.unity3d", map)
  }

  def findDiff(): Unit = {
    // p1里p0没有的
    val p0 = "F:\\HoneySelect"
    val p1 = "G:\\HS\\BackUp\\HoneySelect.20170428.DLC.安装前\\HoneySelect"
//    val p1 = "G:\\HS\\整合\\【糖糖游戏-甜心12.0】\\HoneySelect4K.12.0\\HoneySelect4K"
//    val p1 = "G:\\HS\\整合\\hsv12\\HoneySelect1.1O"
    val listPath = "abdata/list/characustom"
    val r0 = parseDirEx(Paths.get(p0, listPath).toString)
    println(r0.length)
    val r1 = parseDirEx(Paths.get(p1, listPath).toString)
    println(r1.length)

    val writer = new PrintWriter(new File("__diff.txt"))
    //    val s0: Set[String] = r0.map(_._3)(collection.breakOut)
    val s0: Set[String] = r0.map(_.name)(collection.breakOut)
    r1.foreach(r => {
      if (!s0.contains(r.name) && Paths.get(p1, "abdata", r.abdata).toFile.exists()) {
        println(r)
        writer.write(JSON.stringify(List[String](r.id, r.name, r.list, r.abdata)).toString())
        writer.write("\n")
      }
    })
    writer.close()
  }

  def copyNonExist(): Unit = {
    // p1里p0有的都移动出去
    val p0 = "F:\\HoneySelect"
    val p1 = "G:\\HS\\整合\\hsv12\\HoneySelect1.1O"
    val abdata0 = Paths.get(p0, "abdata")
    val abdata1 = Paths.get(p1, "abdata")

    val set0: Set[String] = Kit.walkDir(abdata0.toString, f => {
      if (f.getName.endsWith("unity3d")) {
        abdata0.relativize(Paths.get(f.toString))
      } else {
        null
      }
    }).filter(_ != null).map(_.toString)(collection.breakOut)

    val set1: Set[String] = Kit.walkDir(abdata1.toString, f => {
      if (f.getName.endsWith("unity3d")) {
        abdata1.relativize(Paths.get(f.toString))
      } else {
        null
      }
    }).filter(_ != null).map(_.toString)(collection.breakOut)

    set1.diff(set0).foreach(p => {
      val from = Paths.get(abdata1.toString, p).toString
      val to = Paths.get(abdata1.toString, "__pick", p).toString
      println("Start", from, "=>", to)
      Kit.copy(from, to)
      println("Succ", from, "=>", to)
    })
  }

  def findAll(): Unit = {
    val dir = "F:\\HoneySelect\\abdata\\list\\characustom"
    val rows = parseDirEx(dir)
    println(rows.length)
    val writer = new PrintWriter(new File("__all.txt"))
    val countMap = rows.foldRight(Map[String, Set[String]]())((row, map) => {
      var set: Set[String] = if (map.contains(row.id)) {
        map(row.id)
      } else {
        Set[String]()
      }
      set += row.name
      map + (row.id -> set)
    }).mapValues(_.size)
    rows.sortBy(_.id).foreach(r => {
      if (countMap(r.id) > 1) {
        print(" -- ")
        writer.write(" -- ")
      }
      println(r)
      writer.write(JSON.stringify(List[String](r.id, r.name, r.list, r.abdata)).toString())
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
        writer.write("\n")
      }
      last = r
    })
    writer.close()
  }

  def parseDir(dir: String): List[(String, String, String, String)] = {
    // list id name abdata
    val rows = listFiles(dir).flatMap(file => {
      println(file)
      ParseFile.parseFileSimple(file).map(p => {
        val row = p
        (file.getPath, row(0), row(2), row(4)) // list id name data
      })
    })
    val file = new File(Paths.get(dir, "__list.txt").toString)
    val writer = new PrintWriter(file)
    rows.foreach(row => {
      writer.write(JSON.stringify(List[String](row._1, row._2, row._3, row._4)))
      writer.write("\n")
    })
    writer.close()
    rows
  }

  def parseDirEx(dir: String): List[Parsed] = {
    parseDir(dir).map { case (_1, _2, _3, _4) => Parsed(_1, _2, _3, _4) }
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
