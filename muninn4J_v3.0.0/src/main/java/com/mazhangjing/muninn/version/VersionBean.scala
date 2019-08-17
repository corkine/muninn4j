package com.mazhangjing.muninn.version

import org.springframework.stereotype.Component

@Component
class VersionBean {

  val version: String = "0.0.1"

  val log: String =
    s"""
       |v0.0.1 2019-02-13 混合 Scala。
       |v0.0.0 2019-02-13 Huginn v3 立项。
     """.stripMargin



}

object VersionBean extends App {
  println(new VersionBean().log, new VersionBean().version)
}
