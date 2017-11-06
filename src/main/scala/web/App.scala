package web

import bean.Parser
import io.github.yuemenglong.json.JSON
import io.github.yuemenglong.template.<
import io.github.yuemenglong.template.HTML._
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, ResponseBody}

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

  @ResponseBody
  @RequestMapping(Array(""))
  def index(): String = {
    val js = Source.fromInputStream(App.getClass.getClassLoader.getResourceAsStream("index.jsx")).getLines().mkString("\n")
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
    val mods = Parser.findMods(dir)
    JSON.stringify(mods)
  }

}

object App {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[App])
  }
}
