package net.kgtkr.seekprog.runtime;

import processing.core.PApplet;
import processing.core.PGraphics;
import java.nio.channels.SocketChannel
import java.net.StandardProtocolFamily
import java.nio.file.Path
import java.net.UnixDomainSocketAddress
import processing.event.MouseEvent
import processing.event.KeyEvent
import scala.jdk.CollectionConverters._
import scala.collection.mutable.Buffer
import io.circe._, io.circe.generic.semiauto._, io.circe.parser._,
  io.circe.syntax._
import scala.util.Try

object RuntimeMain {
  var targetFrameCount = 0;
  var events: Vector[List[EventWrapper]] = Vector();

  def run(applet: PApplet, targetFrameCount: Int, events: String) = {
    val renderer = classOf[PGraphicsJava2DRuntime].getName();
    Class.forName(renderer);
    {
      val field = classOf[PApplet].getDeclaredField("renderer");
      field.setAccessible(true);
      field.set(applet, renderer);
    }

    this.targetFrameCount = targetFrameCount;
    this.events = decode[List[List[EventWrapper]]](events).right.get.toVector
  }
}

class RuntimeMain {}
