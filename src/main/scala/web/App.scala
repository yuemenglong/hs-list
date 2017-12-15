package web

import java.io.File
import java.nio.file.Paths

import bean.{Kit, Mod, Parser}
import io.github.yuemenglong.json.JSON
import io.github.yuemenglong.template.Converts._
import io.github.yuemenglong.template.HTML.<
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

import scala.io.Source

/**
  * Created by <yuemenglong@126.com> on 2017/11/3.
  */

@SpringBootApplication
@Controller
@RequestMapping(value = Array(""), produces = Array("application/json"))
@ResponseBody
class App {

  @Value("${dir}")
  var baseDir: String = _

  @Value("${dir1}")
  var baseDir1: String = _

  def render(inner: String): String = {
    val html = <.html.>(
      <.head.>(
        <.script(src = "//cdn.bootcss.com/jquery/2.2.3/jquery.js").>,
        <.script(src = "https://unpkg.com/react/umd/react.development.js").>,
        <.script(src = "https://unpkg.com/react-dom/umd/react-dom.development.js").>,
        <.script(src = "//cdn.bootcss.com/babel-standalone/6.26.0/babel.min.js").>,
        <.script(src = "//cdn.bootcss.com/lodash.js/4.12.0/lodash.js").>,
        <.script(src = "//cdn.bootcss.com/moment.js/2.18.1/moment.js").>,
        <.script(src = "//cdn.bootcss.com/bluebird/3.5.0/bluebird.js").>,
        <.link(ty = "text/css", rel = "stylesheet", href = "https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css").>
      ),
      <.body(className = "").>(inner)
    )
    html.toString
  }


  @ResponseBody
  @RequestMapping(value = Array(""), produces = Array("text/html"))
  def index(): String = {
    val js = Source.fromInputStream(Thread.currentThread().getContextClassLoader.getResourceAsStream("mod.jsx")).getLines().mkString("\n")
    val mods = Parser.findDirMod(s"$baseDir/abdata/list/characustom")
    val dupMap: Map[String, Array[Mod]] = mods.groupBy(_.no)
    val content = <.div.>(
      <.script(ty = "text/babel").>(js),
      <.div.>(
        <.a(id = "refresh").>("刷新"),
        <.a(id = "submit").>("提交"),
        <.div(id = "need-change").>,
      ),
      <.table(className = "table").>(
        mods.zipWithIndex.map { case (m, idx) =>
          val dup = dupMap(m.no).length > 1 match {
            case true => "<+>"
            case false => ""
          }
          <.tr.>(
            <.td.>(dup),
            <.td.>(idx),
            <.td.>(m.no),
            <.td.>(m.name),
            <.td.>(m.list),
            <.td.>(m.data),
            <.td(className = "op").>(
              <.input(ty = "text").>,
              <.a.>("确定")
            ),
          )
        }
      )
    )
    render(content.toString())
  }

  @ResponseBody
  @GetMapping(value = Array("/mods"), produces = Array("application/json"))
  def getMods: String = {
    val mods = Parser.findDirMod(s"$baseDir/abdata/list/characustom")
    JSON.stringify(mods)
  }

  @ResponseBody
  @RequestMapping(value = Array("/diff"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def getDiff: String = {
    val m0 = Parser.findDirMod(s"$baseDir/abdata/list/characustom")
    val m1 = Parser.findDirMod(s"$baseDir1/abdata/list/characustom")
    val diff = Parser.diffMod(m0, m1)
    JSON.stringify(diff)
  }

  @ResponseBody
  @RequestMapping(value = Array("/list/{name}"), method = Array(RequestMethod.PUT))
  def changeNo(@PathVariable name: String, from: String, to: String): Unit = {
    val path = s"$baseDir/abdata/list/characustom/$name.unity3d"
    Parser.modifyNos(path, Array((from, to)))
  }

  @PutMapping(value = Array("/list"))
  def changeNos(@RequestBody body: String): Unit = {
    //[{list,from,to}]
    val arr = JSON.parse(body).asArr().array.map(_.asArr().array.map(_.asStr()))
    arr.map(t => (t(0), (t(1), t(2)))).groupBy(_._1).foreach { case (name, a) =>
      val path = s"$baseDir/abdata/list/characustom/$name"
      val ps = a.map(_._2)
      Parser.modifyNos(path, ps)
    }
  }

  @GetMapping(value = Array("/shoes"))
  def getShoes: String = {
    val dir = Paths.get(baseDir, "Plugins\\Ggmod_cfg").toString
    val ret = JSON.stringify(Kit.scan(new File(dir), _.getName.replace(".cfg", "")))
    println(ret)
    ret
  }

  @GetMapping(value = Array("/shoes/{name}"))
  def getShoesDetail(@PathVariable name: String): String = {
    val cfgPath = Kit.resolve(baseDir, "Plugins/Ggmod_cfg", name + ".cfg")
    val lines = new String(Kit.readFile(cfgPath)).split("\n").map(line => {
      line.trim.split(" ").filter(!_.isEmpty)
    })
    JSON.stringify(lines)
  }

  @PostMapping(Array("/shoes/{name}"))
  def saveShoesDetail(@PathVariable name: String, @RequestBody body: String): String = {
    val arr = JSON.parse(body, classOf[Array[Array[String]]])
    ""
  }

  @GetMapping(value = Array("/shoes/{name}/backup"))
  def backupShoesDetail(@PathVariable name: String): String = {
    val cfgPath = Kit.resolve(baseDir, "Plugins/Ggmod_cfg", name + ".cfg")
    val bakPath = Stream.from(0).map(i => {
      cfgPath + s".$i"
    }).find(p => {
      !Kit.exists(p)
    }).get
    println(s"Backup: $cfgPath -> $bakPath")
    Kit.copy(cfgPath, bakPath)
    "{}"
  }
}

object App {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[App])
  }
}
