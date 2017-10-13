package plotcommunication

import java.nio.ByteBuffer

import actions.MathObjectAction
import boopickle.CompositePickler
import boopickle.Default._
import custommath.{Complex, HolomorphicMap}
import electron.{BrowserWindow, IPCRenderer, NativeImage}
import io.IO
import mathobjects._
import nodejs.{Buffer, Path}
import org.scalajs.dom
import org.scalajs.dom.raw.Event
import plotcommunication.mathobjectmessages._
import renderer.PlotWindow

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.JSConverters._

abstract class Message

object Message {
  implicit val messagePickler: CompositePickler[Message] = compositePickler[Message]
    .addConcreteType[ComplexMessage]
    .addConcreteType[ColorMessage]
    .addConcreteType[TriangleMessage]

    .addConcreteType[SegmentMessage]
    .addConcreteType[EllipseMessage]
    .addConcreteType[LineMessage]
    .addConcreteType[RawShapeMessage]
    .addConcreteType[PolygonMessage]


  private var lastId: Long = 0
  def newId(): Long = {
    lastId += 1
    lastId
  }


  def decode(buffer: scala.scalajs.js.Array[Byte]): Message =
    Unpickle[Message](messagePickler).fromBytes(ByteBuffer.wrap(buffer.toArray))

  def encode(message: Message): scala.scalajs.js.Array[Byte] =
    Buffer.from(Pickle.intoBytes(message).arrayBuffer()).toJSArray.map(_.toByte)

  private var functionToApply: Option[HolomorphicMap] = None


  private val receivedObjects: mutable.Map[Int, MathObject] = mutable.Map()

  private val receivedFunctions: mutable.Queue[(Int, MathObject)] = mutable.Queue()
  private def flush(): Unit = if (functionToApply.isDefined) {
    while (receivedFunctions.nonEmpty) {
      val (id, receivedObject) = receivedFunctions.dequeue()

      receivedObjects.get(id) match {
        case Some(oldObject) =>
          oldObject match {
            case oldObject: Shape =>
              MathObjectAction.removeObject(oldObject)

              val newPiece = MathObject.mapObject(receivedObject, functionToApply.get)

              newPiece match {
                case newPiece: Shape =>
                  val newMathObject = new RawShape(
                    (newPiece.drawTriangles ++ oldObject.drawTriangles).sorted, newPiece.rgb
                  )

                  receivedObjects += (id -> newMathObject)
                  MathObjectAction.addMathObject(newMathObject)
                case _ =>
                  throw new NotImplementedError(s"I should not be here (${newPiece.getClass}")
              }


            case _ =>
              throw new NotImplementedError("I should not receive an object that is not a shape in pieces.")
          }

        case None =>
          val mathObject = MathObject.mapObject(receivedObject, functionToApply.get)
          receivedObjects += (id -> mathObject)
          MathObjectAction.addMathObject(mathObject)
      }
    }
  }

  IPCRenderer.on("map-to-you", (_: Event, functionName: String) => {
    if (scala.scalajs.LinkingInfo.developmentMode) {
      println(s"I will have to apply function `$functionName` to stuff I receive.")
    }

    functionToApply = HolomorphicMap.holomorphicMapsMap.get(functionName)

    flush()
  })

  IPCRenderer.on("plot-communication-message", (_: Event, buffer: Any) => {

    Message.decode(buffer.asInstanceOf[scala.scalajs.js.Array[Byte]]) match {
      case msg: MathObjectMessage =>
        receivedFunctions.enqueue((msg.id.toInt, MathObject.fromMessage(msg)))
        flush()
      case _ =>
    }

  })

  IPCRenderer.on(
    "plot-communication-message-raw-shape",
    (_: Event, id: Int, triangleVertices: js.Array[Double], rgbArray: js.Array[Int]) => {
      val rgb = (rgbArray(0), rgbArray(1), rgbArray(2))

      val triangles = triangleVertices.grouped(6).map(array => {
        val vertices = array.grouped(2).map(couple => Complex(couple(0), couple(1))).toArray
        Triangle(vertices(0), vertices(1), vertices(2))
      })

      receivedFunctions.enqueue((id, new RawShape(triangles.toList, rgb)))
      flush()
    }
  )

  IPCRenderer.on("map", (_: Event, windowId: Int, functionName: String) => {

    if (scala.scalajs.LinkingInfo.developmentMode) {
      println(s"I should map function `$functionName` to plot whose id is $windowId")
    }

    BrowserWindow.fromId(windowId).webContents.send("map-to-you", functionName)

    val (lines, shapes) = MathObject.objects.filter(_.visible).partition(_.isInstanceOf[Line])

    lines.foreach(obj => {
      BrowserWindow.fromId(windowId).webContents.send("plot-communication-message", Message.encode(obj.toMessage))
    })

    shapes.map(_.asInstanceOf[Shape]).foreach(sendShape(newId(), _, windowId))

  })


  private def sendShape(id: Long, shape: Shape, windowId: Int): Unit = {

    val (sendNow, sendLater) = shape.triangles.splitAt(5000)

//    BrowserWindow.fromId(windowId).webContents.send(
//      "plot-communication-message",
//      encode(RawShapeMessage(id, sendNow.map({ case Triangle(v1, v2, v3) => TriangleMessage(v1, v2, v3)}), shape.rgb))
//    )

    BrowserWindow.fromId(windowId).webContents.send(
      "plot-communication-message-raw-shape",
      id.toInt, sendNow.flatMap({ case Triangle(Complex(x1, y1), Complex(x2, y2), Complex(x3, y3)) => js.Array[Double](
        x1, y1, x2, y2, x3, y3
      )}).toJSArray, js.Array[Int](shape.rgb._1, shape.rgb._2, shape.rgb._3)
    )

    if (sendLater.nonEmpty) {
      js.timers.setTimeout(50) {
        sendShape(id, new RawShape(sendLater, shape.rgb), windowId)
      }
    }

  }


  IPCRenderer.on("focus", (_: Event) => {
    PlotWindow.canvas.style.border = "2px solid red"
  })

  IPCRenderer.on("blur", (_: Event) => {
    PlotWindow.canvas.style.border = "2px solid black"
  })


  IPCRenderer.on("save-as-png", (_: Event) => {
    val data = PlotWindow.canvas.toDataURL("image/png")

    val png = NativeImage.createFromDataURL(data).toPNG()

    IO.mkdir("images")

    var success = true
    IO.writeFile("images/plot.png", png, (_) => success = false)

    if (success) {
      dom.window.alert("Images successfully saved at\n" + Path.join(IO.baseDirectory, "/images/plot.png"))
    } else {
      dom.window.alert("Error while creating the image...")
    }

  })
}
