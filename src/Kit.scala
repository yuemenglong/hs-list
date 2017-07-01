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
