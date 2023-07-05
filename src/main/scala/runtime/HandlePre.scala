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

object HandlePre {
  def apply(applet: PApplet, targetFrameCount: Int, events: String) = {
    val sockPath = Path.of(System.getProperty("seekprog.sock"));
    val sockAddr = UnixDomainSocketAddress.of(sockPath);
    val socketChannel = SocketChannel.open(StandardProtocolFamily.UNIX);
    socketChannel.connect(sockAddr);

    applet.frameRate(1e+9f + 1);
    val handlePre = new HandlePre(
      applet,
      targetFrameCount,
      socketChannel,
      decode[List[List[EventWrapper]]](events).right.get.toVector
    );
    applet.registerMethod("pre", handlePre);
    applet.registerMethod("mouseEvent", handlePre);
    applet.registerMethod("keyEvent", handlePre);
  }
}

class HandlePre(
    applet: PApplet,
    targetFrameCount: Int,
    socketChannel: SocketChannel,
    reproductionEvents: Vector[List[EventWrapper]]
) {
  var gBak: Option[PGraphics] = None;
  var onTarget = false;
  val currentFrameEvents = Buffer[EventWrapper]();
  val eventsBuf = Buffer[List[EventWrapper]]();
  var stopReproductionEvent = false;

  def pre() = {
    if (this.applet.frameCount == 1) {
      this.gBak = Some(this.applet.g);
      this.applet.g = new PGraphicsDummy();
    }

    if (!this.onTarget && this.applet.frameCount >= this.targetFrameCount) {
      this.applet.g.dispose();
      this.applet.g = this.gBak.get;
      this.onTarget = true;
      this.applet.frameRate(60);
      socketChannel.write(
        RuntimeEvent.OnTargetFrameCount
          .toBytes()
      )
    }

    if (!this.stopReproductionEvent) {
      Try(this.reproductionEvents(this.applet.frameCount - 1)).toOption
        .foreach {
          _.foreach {
            case EventWrapper.Mouse(evt) => this.applet.postEvent(evt.toPde());
            case EventWrapper.Key(evt)   => this.applet.postEvent(evt.toPde());
          };
        };
    }

    if (this.onTarget) {
      this.eventsBuf += this.currentFrameEvents.toList;
      this.currentFrameEvents.clear();
    }

    if (this.onTarget && this.applet.frameCount % 60 == 0) {
      socketChannel.write(
        RuntimeEvent
          .OnUpdateLocation(
            this.applet.frameCount,
            this.stopReproductionEvent,
            this.eventsBuf.toList
          )
          .toBytes()
      )
      this.eventsBuf.clear();
    }
  }

  def mouseEvent(evt: MouseEvent) = {
    if (this.onTarget) {
      this.currentFrameEvents +=
        EventWrapper.Mouse(MouseEventWrapper.fromPde(evt));
      if (evt.getNative() ne ReproductionEvent) {
        this.stopReproductionEvent = true;
      }
    }
  }

  def keyEvent(evt: KeyEvent) = {
    if (this.onTarget) {
      this.currentFrameEvents +=
        EventWrapper.Key(KeyEventWrapper.fromPde(evt));
      if (evt.getNative() ne ReproductionEvent) {
        this.stopReproductionEvent = true;
      }
    }
  }
}
