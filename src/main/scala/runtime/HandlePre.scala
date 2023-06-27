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

object HandlePre {
  def apply(applet: PApplet, targetFrameCount: Int) = {
    val sockPath = Path.of(System.getProperty("seekprog.sock"));
    val sockAddr = UnixDomainSocketAddress.of(sockPath);
    val socketChannel = SocketChannel.open(StandardProtocolFamily.UNIX);
    socketChannel.connect(sockAddr);

    applet.frameRate(1e+9f + 1);
    val handlePre = new HandlePre(applet, targetFrameCount, socketChannel);
    applet.registerMethod("pre", handlePre);
    applet.registerMethod("mouseEvent", handlePre);
    applet.registerMethod("keyEvent", handlePre);
  }
}

class HandlePre(
    applet: PApplet,
    targetFrameCount: Int,
    socketChannel: SocketChannel
) {
  var gBak: Option[PGraphics] = None;
  var onTarget = false;
  val currentFrameEvents = Buffer[EventWrapper]();
  val eventsBuf = Buffer[List[EventWrapper]]();

  def pre() = {
    if (this.applet.frameCount == 1) {
      this.gBak = Some(this.applet.g);
      this.applet.g = new PGraphics();
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

    if (this.onTarget) {
      this.eventsBuf += this.currentFrameEvents.toList;
      this.currentFrameEvents.clear();
    }

    if (this.onTarget && this.applet.frameCount % 60 == 0) {
      socketChannel.write(
        RuntimeEvent
          .OnUpdateLocation(
            this.applet.frameCount.toDouble / 60,
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
    }
  }

  def keyEvent(evt: KeyEvent) = {
    if (this.onTarget) {
      this.currentFrameEvents +=
        EventWrapper.Key(KeyEventWrapper.fromPde(evt));
    }
  }
}
