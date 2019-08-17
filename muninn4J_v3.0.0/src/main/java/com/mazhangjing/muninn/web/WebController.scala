package com.mazhangjing.muninn.web

import java.util.{HashMap => JMap}

import com.mazhangjing.muninn.version.VersionBean
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{ExceptionHandler, RequestMapping, RestController}
import org.springframework.web.servlet.ModelAndView

@RestController
class WebController {

  @Autowired var versionBean: VersionBean = _

  private val logger = LoggerFactory.getLogger(classOf[WebController])

  @RequestMapping(Array("/")) def index = "Home of Muninn ApiController"

  @RequestMapping(Array("/log")) def version: JMap[String, String] = {
    val map = new JMap[String, String]
    map.put("version", versionBean.version)
    map.put("log", versionBean.log)
    map
  }

  @ExceptionHandler(Array(classOf[RuntimeException])) def handler(exception: Exception): ModelAndView = {
    logger.warn(s"Handler Handle Exception $exception")
    val mv = new ModelAndView("exception")
    mv.addObject("error", exception.getMessage)
    mv
  }
}
