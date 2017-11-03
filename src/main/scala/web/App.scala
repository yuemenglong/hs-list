package web

import io.github.yuemenglong.template.HTML._
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMapping, ResponseBody}

import scala.io.Source

/**
  * Created by <yuemenglong@126.com> on 2017/11/3.
  */

@SpringBootApplication
@Controller
@RequestMapping(Array(""))
class App {

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
      ),
      <.body.>(
        <.div(id = "root").>,
        <.script(ty = "text/babel").>(js)
      )
    )
    html.toString()
  }

}

object App {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[App])
  }
}
