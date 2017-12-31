package bean

import java.io.File
import java.nio.file.Paths


/**
  * Created by <yuemenglong@126.com> on 2017/11/3.
  */

class ListFile {
  var fileName: String = _
}

class ListFileBak {
  var fileName: String = _
}

class Mod {
  var id: String = _
  var no: String = _
  var ty: String = _
  var name: String = _
  var dir: String = _
  var list: String = _
  var data: String = _
  var start: Integer = _

  override def toString: String = f"$no $ty $name $list $dir $data $start%X"
}

object Parser {

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
        mod.data = items(4)
        mod.start = m.start + 1
        mod
      }
    })
  }

  def findMod(data: Array[Byte]): Array[Mod] = {
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
      if (items.length == 4) {
        val mod = new Mod
        mod.no = items(0)
        mod.ty = items(1)
        mod.name = items(2)
        mod.data = items(3)
        mod.dir = "_"
        mod.start = start
        mod
      } else if (items.length == 5) {
        val mod = new Mod
        mod.no = items(0)
        mod.ty = items(1)
        mod.name = items(2)
        mod.dir = items(3)
        mod.data = items(4)
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

  def findDirMod(dir: String): Array[Mod] = {
    Kit.scan(new File(dir), f => {
      if (!f.getName.endsWith(".unity3d")) {
        Array[Mod]()
      } else {
        findMod(Kit.readFile(f)).map(m => {
          m.list = f.getName
          m
        })
      }
    }).flatten.sortBy(_.no)
  }

  def diffMod(m1: Array[Mod], m2: Array[Mod]): Array[Mod] = {
    val map1 = m1.map(m => (m.name, m)).toMap
    val map2 = m2.map(m => (m.name, m)).toMap
    val diffSet = map2.keySet.diff(map1.keySet)
    diffSet.map(map2(_)).toArray.sortBy(_.no)
  }

  def modifyNos(path: String, pairs: Array[(String, String)]): Unit = {
    // 先备份
    val bak = Stream.from(0)
      .map(i => s"$path.bak$i")
      .find(p => !new File(p).exists()).get
    val content = Kit.readFile(path)
    val mods = findMod(content)
    pairs.foreach { case (from, to) =>
      val target = mods.find(_.no == from).get
      to.getBytes.zipWithIndex.foreach { case (b: Byte, i: Int) =>
        val idx = target.start + i
        require(content(idx) == from(i))
        print(content(idx).toChar, b.toChar)
        content(idx) = b
      }
      println()
    }
    println(path, bak)
    Kit.rename(path, bak)
    Kit.writeFile(path, content)
    println("SUCC")
  }

  def fileExists(path: String): Boolean = {
    new File(path).exists()
  }

  def backupMod(hsDir: String, backupDir: String, modDir: String): Unit = {
    val fileDir = Paths.get(modDir).getFileName.toString
    val backupFileDir = Paths.get(backupDir, fileDir).toString
    if (fileExists(backupFileDir)) {
      throw new RuntimeException(s"备份路径已经存在, $backupFileDir")
    }
    if (!fileExists(Paths.get(hsDir, "abdata").toString)) {
      throw new RuntimeException(s"HS路径下没有abdata, $hsDir")
    }
    if (!fileExists(Paths.get(modDir, "abdata").toString)) {
      throw new RuntimeException(s"Mod路径下没有abdata, $modDir")
    }
    val modRoot = Paths.get(modDir)
    val hsRoot = Paths.get(hsDir)
    val backupFileRoot = Paths.get(backupFileDir)
    Kit.scan(new File(modDir), file => {
      val rel = modRoot.relativize(Paths.get(file.getAbsolutePath)).toString
      val hsPath = hsRoot.resolve(rel).toString
      val backupPath = backupFileRoot.resolve(rel).toString
      if (fileExists(hsPath)) {
        println(s"""$hsPath -> $backupPath""")
        Kit.copy(hsPath, backupPath)
      } else {
        println(s"""$hsPath Not Exists""")
      }
    })
  }

  def mergeAbdata(dir: String): Unit = {
    val dest = Paths.get(dir, "abdata").toString
    require(Kit.exists(dest))

  }

  def main(args: Array[String]): Unit = {
    //    pickupMod("D:/hs/h0", "D:/hs/mod", "D:/hs/backup/mod-0")
    val modPath = "D:\\BaiduNetdiskDownload\\[转帖] 衣服和一些朵品（听起来像淘宝 ( ′ ▽ ` ) )[15图]-clothes 2\\Daydream_tattoo_pack\\操门那只英雄hs正式版纹身摸底";
    backupMod("F:/HoneySelect",
      "D:/Game/HoneySelectMod/backup",
      modPath,
    )
    //    val bs = Kit.readFile("D:/list/0/characustom/00.unity3d")
    //    val s = new String(bs).map(c => {
    //      if (Kit.isPrintable(c) || c.toInt >= 128) c
    //      else ' '
    //    }).mkString("")
    //    Kit.writeFile("str.txt", s.getBytes())
    //    Kit.writeFile("str.txt", s.getBytes())

    //    val res = Kit.scan(new File("D:\\list\\0\\characustom"), f => {
    //      println(f.getName)
    //      findMod(Kit.readFile(f))
    //    }).flatten
    //    res.filter(m => m.dir == "").foreach(println)

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
