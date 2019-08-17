package com.mazhangjing.muninn.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.{CorsRegistry, DefaultServletHandlerConfigurer, ViewControllerRegistry, WebMvcConfigurer}

@Configuration
class WebConfig extends WebMvcConfigurer {

  private val views: Map[String, String] =
    Map(
      "/" -> "index",
      "/about" -> "about",
    )

  /**
    * 添加跨域设置，允许所有来源的所有方法，3600s 的 MaxAge
    * @param registry 跨域注册器
    */
  override def addCorsMappings(registry: CorsRegistry): Unit = {
    registry.addMapping("*").allowCredentials(true)
      .allowedHeaders("*").allowedMethods("*")
      .allowedOrigins("*").maxAge(3600)
  }

  /**
    * 添加静态 URL 映射，映射定义在 views Map 中
    * @param registry ViewController 注册器
    */
  override def addViewControllers(registry: ViewControllerRegistry): Unit = {
    views.foreach(view => registry.addViewController(view._1).setViewName(view._2))
  }

  /**
    * 启用静态 Servlet 以匹配静态资源访问
    * @param configurer Servlet 注册器
    */
  override def configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer): Unit =
    configurer.enable()
}