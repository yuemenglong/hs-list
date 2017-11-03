import java.io.FileInputStream
import java.nio.file.{Files, Paths}

import io.github.yuemenglong.orm.lang.anno.{Entity, Id}

import scala.io.Source

/**
  * Created by <yuemenglong@126.com> on 2017/11/3.
  */

@Entity
class ListFile {
  var fileName: String = _
}

@Entity
class ListFileBak {
  var fileName: String = _
}

@Entity
class Mod {
  @Id
  var id: Long = _
  var no: String = _
  var ty: Integer = _
  var name: String = _
  var abdata: String = _
  var u3d: String = _
}


object Parse2 {

  def findUnity(content: Array[Byte], from: Int): Int = {
    content.indexOfSlice(".unity3d", from)
  }

  def main(args: Array[String]): Unit = {
    val uri = Thread.currentThread().getContextClassLoader.getResource("zeaska mod package1.unity3d").toURI
    val content = Kit.readFile(uri.getPath)
    val contentStr = content.map(_.toChar).map(c =>
      if (c == ' ') '.'
      else if (Kit.isPrintable(c)) c.toString
      else ' '
    ).mkString("")
    //    Kit.writeFile("str.txt", contentStr.getBytes())
    val re =
      """ \d{6,} (\d+ [^ ]+ [^ ]+ [^ ]+)? """.r
    re.findAllMatchIn(contentStr).toArray.foreach(m => {
      println(m.group(0), m.start, m.end)
    })
    // 连续6位数字
    //    println(content.length)
    //    var from = 0
    //    var pos = findUnity(content, from)
    //    println(pos)
    //    val bytes = Files.readAllBytes(Paths.get(path))
    //    println(bytes.length)
  }
}
