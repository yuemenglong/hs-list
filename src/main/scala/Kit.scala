import java.io.{File, FileInputStream, FileOutputStream}

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

  def readFile(path: String): Array[Byte] = {
    val fs = new FileInputStream(path)
    val ret = Stream.continually(fs.read()).takeWhile(_ != -1).map(_.toByte).toArray
    fs.close()
    ret
  }

  def writeFile(path: String, content: Array[Byte]) = {
    val fs = new FileOutputStream(path)
    fs.write(content)
    fs.close()
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

}
