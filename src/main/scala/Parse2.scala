import java.io.{File, FileInputStream}
import java.nio.file.{Files, Paths}

import io.github.yuemenglong.json.JSON
import io.github.yuemenglong.orm.lang.anno.{Entity, Id}
import io.github.yuemenglong.orm.lang.types.Types._

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
  var id: String = _
  var no: String = _
  var ty: String = _
  var name: String = _
  var dir: String = _
  var u3d: String = _
  var start: Integer = _

  override def toString: String = s"$no $ty $name $dir $u3d"
}


object Parse2 {

  def findUnity(content: Array[Byte], from: Int): Int = {
    content.indexOfSlice(".unity3d", from)
  }

  def normalize(data: Array[Byte]): String = {
    data.map(_.toChar).map(c =>
      if (c == ' ') '.'
      else if (Kit.isPrintable(c) || c.toInt > 127) c
      else ' '
    ).mkString("")
  }

  def findMod2(data: Array[Byte]): Array[Mod] = {
    val re =
      """ \d{6,} (\d+ [^ ]+ [^ ]+ [^ ]+ )?""".r
    val nor = normalize(data)
    re.findAllMatchIn(nor).toArray.map(m => {
      val line = nor.slice(m.start + 1, m.end - 1)
      if (line.split(" ").length != 5) {
        println(line, f"${m.start + 1}%X")
        new Mod
        //        require(false)
      } else {
        val items =
          """[^ ]+""".r.findAllMatchIn(line).map(m2 => {
            data.slice(m.start + 1 + m2.start, m.start + 1 + m2.end).map(_.toChar).mkString("")
          }).toArray
        val mod = new Mod
        mod.no = items(0)
        mod.ty = items(1)
        mod.name = items(2)
        mod.dir = items(3)
        mod.u3d = items(4)
        mod.start = m.start + 1
        mod
      }
    })
  }

  def findMod(data: Array[Byte]): Array[Mod] = {
    val re =
      """ \d{6,} .+?\.unity3d""".r
    val nre = """ \d{6,} """.r
    val nor = normalize(data)
    re.findAllMatchIn(nor).toArray.map(m => {
      val line = nor.slice(m.start, m.end).reverse
      val m2 = nre.findFirstMatchIn(line).get
      val start = m.end - m2.end + 1
      val end = m.end
      val buf = data.slice(start, end)
      val items = Kit.splitBy(buf, 0x09).map(new String(_))
      if (items.length == 4) {
        val mod = new Mod
        mod.no = items(0)
        mod.ty = items(1)
        mod.name = items(2)
        mod.u3d = items(3)
        mod.dir = "_"
        mod.start = start
        mod
      } else if (items.length == 5) {
        val mod = new Mod
        mod.no = items(0)
        mod.ty = items(1)
        mod.name = items(2)
        mod.dir = items(3)
        mod.u3d = items(4)
        mod.start = start
        mod
      } else {
        println(new String(buf))
        println(items(0), items.length, f"${m.start + 1}%X")
        require(false)
        null
      }
    }).filter(_ != null)
  }

  def main(args: Array[String]): Unit = {
    //    val bs = Kit.readFile("D:/list/0/characustom/00.unity3d")
    //    val s = new String(bs).map(c => {
    //      if (Kit.isPrintable(c) || c.toInt >= 128) c
    //      else ' '
    //    }).mkString("")
    //    Kit.writeFile("str.txt", s.getBytes())
    //    Kit.writeFile("str.txt", s.getBytes())

    val res = Kit.scan(new File("D:\\list\\0\\characustom"), f => {
      println(f.getName)
      findMod(Kit.readFile(f))
    }).flatten
    res.filter(m => m.dir == "").foreach(println)

    //    val uri = Thread.currentThread().getContextClassLoader.getResource("zeaska mod package1.unity3d").toURI
    //    val content = Kit.readFile(uri.getPath)
    //    findMod(content).foreach(println)
    //    val contentStr = content.map(_.toChar).map(c =>
    //      if (c == ' ') '.'
    //      else if (Kit.isPrintable(c)) c.toString
    //      else ' '
    //    ).mkString("")
    //    Kit.writeFile("str.txt", contentStr.getBytes())

    // 连续6位数字
    //    println(content.length)
    //    var from = 0
    //    var pos = findUnity(content, from)
    //    println(pos)
    //    val bytes = Files.readAllBytes(Paths.get(path))
    //    println(bytes.length)
  }
}
