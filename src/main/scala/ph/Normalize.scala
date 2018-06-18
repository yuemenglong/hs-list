package ph

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.Paths

import org.apache.commons.io.FileUtils

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
  * Created by yml on 2017/7/1.
  */
object Normalize {

  def exec(dir: String): Unit = {
    if (!new File(dir).isDirectory) {
      throw new Exception(s"${dir} Not Directory")
    }

    def nameFilter(f: File): Boolean = {
      Array("abdata", "plugins").contains(f.getName)
    }

    def go(base: File, cur: File): Unit = {
      cur.listFiles().filter(_.isDirectory) match {
        case fs if fs.count(nameFilter) > 0 => // 找到abdata层
          if (base != cur) {
            cur.listFiles().foreach(FileUtils.moveToDirectory(_, base, false))
            //            FileUtils.copyToDirectory(cur, base)
            cur.delete()
            println(s"Move [${cur}] To [${base}]")
          }
        case fs if fs.length == 1 => go(base, fs(0))
        case _ => throw new Exception("Can't Find abdata")
      }
    }

    new File(dir).listFiles().filter(_.isDirectory).foreach(f => go(f, f))
  }

  def main(args: Array[String]): Unit = {
    val dir = "C:\\Users\\yml\\Desktop\\playhome"
    while (true) {
      println("Cycle")
      exec(dir)
      Thread.sleep(1000)
    }
  }


}
