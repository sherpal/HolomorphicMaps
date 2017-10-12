package io

import boopickle.Default._
import boopickle.CompositePickler

sealed abstract class FileContent


object FileContent {
  implicit val fileContentPickler: CompositePickler[FileContent] = {
    compositePickler[FileContent]
      .addConcreteType[ConnectionToGameInfo]
      .addConcreteType[ControlBindings]
  }

}


final case class ConnectionToGameInfo(pseudo: String,
                                      gameName: String,
                                      address: String,
                                      port: Int) extends FileContent

final case class ControlBindings(up: Int, down: Int, left: Int, right: Int,
                                 abilities: List[Int]) extends FileContent