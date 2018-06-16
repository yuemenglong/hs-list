package ph

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.Paths

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
  * Created by yml on 2017/7/1.
  */
object Kit {

  def splitBy(buffer: Array[Byte], flag: Byte): Array[Array[Byte]] = {
    val pos = buffer.indexOf(flag)
    pos match {
      case -1 => Array(buffer)
      case _ =>
        val spl = buffer.splitAt(pos)
        Array(spl._1) ++ splitBy(spl._2.drop(1), flag)
    }
  }

  def scan[T: ClassTag](dir: File, fn: (File) => T): Array[T] = {
    if (dir.isDirectory) {
      dir.listFiles().flatMap(scan(_, fn))
    } else {
      Array(fn(dir))
    }
  }

  def readFile2(path: String): Array[Byte] = {
    val fs = new FileInputStream(path)
    val buffer = new Array[Byte](4096)
    var ret = ArrayBuffer[Byte]()
    var len = fs.read(buffer)
    while (len >= 0) {
      ret ++= buffer.slice(0, len)
      len = fs.read(buffer)
    }
    //    val ret = Stream.continually(fs.read(buffer)).takeWhile(_ != -1).map(_.toByte).toArray
    fs.close()
    ret.toArray
  }

  def readFile(path: String): Array[Byte] = {
    val fs = new FileInputStream(path)
    val ret = Stream.continually({
      val buffer = new Array[Byte](4096)
      (fs.read(buffer), buffer)
    }).takeWhile(_._1 >= 0).flatMap(p => p._2.take(p._1)).toArray
    fs.close()
    ret
  }

  def readFile(file: File): Array[Byte] = {
    val fs = new FileInputStream(file)
    val ret = Stream.continually({
      val buffer = new Array[Byte](4096)
      (fs.read(buffer), buffer)
    }).takeWhile(_._1 >= 0).flatMap(p => p._2.take(p._1)).toArray
    fs.close()
    ret
  }

  def resolve(dir: String, paths: String*): String = {
    Paths.get(dir, paths: _*).toString
  }

  val charSet: Set[Char] = """`~!@#$%^&*()_+=-{}|[]\"':;?><,./""".toSet

  def isPrintable(c: Char): Boolean =
    '0' <= c && c <= '9' ||
      'a' <= c && c <= 'z' ||
      'A' <= c && c <= 'Z' ||
      charSet.contains(c)

  def writeFile(path: String, content: Array[Byte]): Unit = {
    val fs = new FileOutputStream(path)
    fs.write(content)
    fs.close()
  }

  def copy(from: String, to: String): Unit = {
    new File(to).getParentFile.mkdirs()
    require(exists(from))
    require(!exists(to))
    val is = new FileInputStream(from)
    val os = new FileOutputStream(to)
    val buffer = new Array[Byte](4096)
    var ret = 0
    while (ret >= 0) {
      ret = is.read(buffer)
      if (ret > 0) {
        os.write(buffer, 0, ret)
      }
    }
    is.close()
    os.close()
  }

  def exists(path: String): Boolean = {
    new File(path).exists()
  }

  def rename(from: String, to: String): Unit = {
    if (!from.equals(to)) { //新的文件名和以前文件名不同时,才有必要进行重命名
      val fromFile = new File(from)
      val toFile = new File(to)
      if (!fromFile.exists) return //重命名文件不存在
      require(!toFile.exists())
      fromFile.renameTo(toFile)
    }
  }

  def mkdir(path: String): Unit = {
    val dir = new File(path)
    dir.mkdirs()
  }

  def splitBySlice(buffer: Array[Byte], flag: Seq[Byte]): Array[Array[Byte]] = {
    val pos = buffer.indexOfSlice(flag)
    pos match {
      case -1 => Array(buffer)
      case _ =>
        val spl = buffer.splitAt(pos)
        Array(spl._1) ++ splitBySlice(spl._2.drop(flag.length), flag)
    }
  }

  def printBinary(buffer: Array[Byte]): Unit = {
    buffer.foreach(b => {
      if (printAble(b)) {
        print("%c".format(b))
      } else {
        print("%02X".format(b))
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

  def walkDir(path: String, fn: (File) => Object): Array[Object] = {
    def go(file: File, fn: (File) => Object): Array[Object] = {
      if (file.isDirectory) {
        file.listFiles().flatMap(go(_, fn))
      } else {
        Array(fn(file))
      }
    }

    go(new File(path), fn)
  }

}
