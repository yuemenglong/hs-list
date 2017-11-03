import java.io.File

/**
  * Created by yml on 2017/7/2.
  */
object ReplaceNo {
  def replace(path: String, map: Map[String, String]): Unit = {
    // 先备份
    val bak = Stream.from(0)
      .map(i => s"$path.bak$i")
      .find(p => !new File(p).exists()).get
    val content = Kit.readFile2(path)
    val matches = ParseFile.parseFile(new File(path)).filter(rows => {
      map.contains(rows(0)._3)
    })
    if (matches.length == 0) {
      return
    }
    matches.foreach(rows => {
      val oldSeq = rows(0)._3
      val newSeq = map(oldSeq)
      val start = rows(0)._1
      for (i <- 0 until oldSeq.length) {
        val oldVal = content(start + i)
        val newVal = newSeq(i).toByte
        println(oldVal, newVal)
        println(f"$oldVal%c, $newVal%c")
        content(start + i) = newVal
      }
    })
    Kit.rename(path, bak)
    Kit.writeFile(path, content)
  }
}
