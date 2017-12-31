package bean

import java.io.File
import java.nio.file.Paths

/**
  * Created by <yuemenglong@126.com> on 2017/8/27.
  */
object CopyListAndData {
  def go(fromDir: String, toDir: String, listName: String): Unit = {
    val listFile = Paths.get(fromDir, "abdata/list/characustom", listName).toFile
    require(listFile.exists())
    val from = listFile.toString
    val to = Paths.get(toDir, "abdata/list/characustom", listName).toString
    println(s"Start Copy $from => $to")
    if (new File(from).exists() && !new File(to).exists()) {
      Kit.copy(from, to)
      println(s"Finish Copy $from => $to")
    } else {
      println("Error")
    }
    ParseFile.parseFileSimpleEx(listFile).foreach(p => {
      // 复制每个abdata文件
      val from = Paths.get(fromDir, "abdata", p.abdata).toString
      val to = Paths.get(toDir, "abdata", p.abdata).toString
      println(s"Start Copy $from => $to")
      if (new File(from).exists() && !new File(to).exists()) {
        Kit.copy(from, to)
        println(s"Finish Copy $from => $to")
      } else {
        println("Error")
      }
    })

  }
}
