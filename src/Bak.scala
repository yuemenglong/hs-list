import java.io.{File, FileInputStream}

import Main.{printAble, printBinary, splitBy, splitBySlice}

/**
  * Created by Administrator on 2017/6/22.
  */
class Bak {
  def parseFile(file: File): Unit = {
    val is = new FileInputStream(file)
    val buffer = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
    val anchor = buffer.indexOfSlice(".unity3d")

    val start = buffer.lastIndexOfSlice(List(0xb, 0, 0, 0), anchor) + 4
    val end = buffer.indexOf(0x0a, start)
    val line = buffer.drop(start).take(end - start)
    val items = splitBy(line, 0x09)
    //    val segFlag: List[Byte] = List(0, 0, 0, 0, 0xf, 0, 0, 0)
    val segFlag: List[Byte] = List(0x0a, 0x00)

    val segments = splitBySlice(buffer.drop(start), segFlag).map(segment => {
      // 分离前面不可打印的部分
      val (_, t1) = segment.splitAt(segment.indexWhere(printAble))
      val (header, t2) = t1.splitAt(t1.indexWhere(!printAble(_)))
      printBinary(header)

      val spl = splitBySlice(t2, List(0, 0))
      println(spl(0).length)
      val rest = splitBySlice(t2, List(0, 0))(1)

      val lines = splitBy(rest, 0x0a)
      lines.foreach(line => {
        val items = splitBy(line, 0x09)
        println(new String(items(0)))
      })

      //      println()

    })
    println(segments.length)
  }
}
