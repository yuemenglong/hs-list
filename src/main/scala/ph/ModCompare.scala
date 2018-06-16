package ph

import java.io.File
import java.nio.file.Paths

object ModCompare {

  def compare(modDir: String, gameDir: String): Unit = {
    require(new File(modDir).isDirectory)
    require(new File(gameDir + "/abdata").isDirectory)

    def files(dir: File): Array[File] = {
      if (dir.isDirectory) {
        dir.listFiles().flatMap(files)
      } else {
        Array(dir)
      }
    }

    new File(modDir).listFiles().foreach(sub => {
      if (sub.isDirectory) {
        files(sub).foreach(f => {
          val rel = Paths.get(sub.toString).relativize(Paths.get(f.getAbsolutePath)).toString
          val gf = Paths.get(gameDir, rel).toFile
          if (!gf.exists()) {
            println(s"Not Exists ${f}")
          } else if (f.length() != gf.length()) {
            println(s"Not Same ${f}")
          }
        })
      }
    })
  }

  def main(args: Array[String]): Unit = {
    val gameDir = "C:\\Users\\yml\\Desktop\\playhome"
    val modDir = gameDir + "/pick"
    compare(modDir, gameDir)
  }

}
