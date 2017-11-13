package web

import bean.Parser
import io.github.yuemenglong.json.JSON
import io.github.yuemenglong.template.<
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


  @ResponseBody
  @RequestMapping(Array(""))
  def index(): String = {

    val js = Source.fromInputStream(App.getClass.getClassLoader.getResourceAsStream("mods.jsx")).getLines().mkString("\n")
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
      <.body(className = "").>(
        <.div(id = "root").>,
        <.script(ty = "text/babel").>(js)
      )
    )
    html.toString()
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
