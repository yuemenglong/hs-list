package web

import bean.{Mod, Parser}
import io.github.yuemenglong.json.JSON
import io.github.yuemenglong.template.HTML.<
import io.github.yuemenglong.template.Converts._
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, ResponseBody}

import scala.io.Source

/**
  * Created by <yuemenglong@126.com> on 2017/11/3.
  */

@SpringBootApplication
@Controller
@RequestMapping(Array(""))
class App {

  @Value("${dir}")
  var dir: String = _

  @Value("${dir1}")
  var dir1: String = _

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
  @RequestMapping(Array(""))
  def index(): String = {
    val js = Source.fromInputStream(Thread.currentThread().getContextClassLoader.getResourceAsStream("mod.jsx")).getLines().mkString("\n")
    val mods = Parser.findDirMod(s"$dir/abdata/list/characustom")
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
  @RequestMapping(value = Array("/mods"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def getMods: String = {
    val mods = Parser.findDirMod(s"$dir/abdata/list/characustom")
    JSON.stringify(mods)
  }

  @ResponseBody
  @RequestMapping(value = Array("/diff"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def getDiff: String = {
    val m0 = Parser.findDirMod(s"$dir/abdata/list/characustom")
    val m1 = Parser.findDirMod(s"$dir1/abdata/list/characustom")
    val diff = Parser.diffMod(m0, m1)
    JSON.stringify(diff)
  }

  @ResponseBody
  @RequestMapping(value = Array("/list/{name}"), method = Array(RequestMethod.PUT))
  def changeNo(@PathVariable name: String, from: String, to: String): Unit = {
    val path = s"$dir/abdata/list/characustom/$name.unity3d"
    Parser.modifyNo(path, from, to)
  }
}

object App {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[App])
  }
}
