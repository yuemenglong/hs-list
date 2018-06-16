package ph

import java.io.{File, FileInputStream}
import java.nio.file.Paths


/**
  * Created by <yuemenglong@126.com> on 2017/11/3.
  */

class UList {
  var no: String = _
  var ty: String = _
  var name: String = _
  var abdata: String = _
  var list: String = _
  var mod: String = _
  var start: Integer = _

  override def toString: String = f"$no $ty $name $abdata $mod $list $start%X"
}

object ListReader {

  def normalize(data: Array[Byte]): String = {
    data.map(_.toChar).map(c =>
      if (c == ' ') '.'
      else if (Kit.isPrintable(c) || c.toInt > 127) c
      else ' '
    ).mkString("")
  }

  def read(path: String): Array[Byte] = {
    val is = new FileInputStream(path)
    val buf = new Array[Byte](4096)
    Stream.continually(is.read(buf))
      .takeWhile(_ >= 0).flatMap(buf.take(_)).toArray
  }

  def listMod(path: String): Array[UList] = {
    val data: Array[Byte] = read(path)
    val re =
      """ \d{6,} .+?\.unity\.?3d""".r
    val nre = """ \d{6,} """.r
    val nor = normalize(data)
    re.findAllMatchIn(nor).toArray.map(m => {
      val line = nor.slice(m.start, m.end).reverse
      val m2 = nre.findFirstMatchIn(line).get
      val start = m.end - m2.end + 1
      val end = m.end
      val buf = data.slice(start, end)
      val items = Kit.splitBy(buf, 0x09).map(new String(_))
      val list = new UList
      list.list = path
      if (items.length == 4) {
        list.no = items(0)
        list.ty = items(1)
        list.name = items(2)
        list.mod = items(3)
        list.abdata = "_"
        list.start = start
        list
      } else if (items.length == 5) {
        list.no = items(0)
        list.ty = items(1)
        list.name = items(2)
        list.abdata = items(3)
        list.mod = items(4)
        list.start = start
        list
      } else {
        println(new String(buf))
        println(items(0), items.length, f"${m.start + 1}%X")
        require(false)
        null
      }
    }).filter(_ != null)
  }

  def main(args: Array[String]): Unit = {
    listMod("C:\\Users\\yml\\Desktop\\playhome\\[MOD]+[移植]+HS的2双高跟鞋，绑带细高跟，高防水台，蛮配衣服的[2图]+\\zDA ggmod19-20\\abdata\\list\\characustom/1.unity3d")
      .foreach(println)
  }
}
