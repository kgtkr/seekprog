package net.kgtkr.seekprog.runtime;

import processing.core.PApplet;
import processing.core.PGraphics;
import java.nio.channels.SocketChannel
import java.net.StandardProtocolFamily
import java.nio.file.Path
import java.net.UnixDomainSocketAddress

object HandlePre {
  def apply(applet: PApplet, targetFrameCount: Int) = {
    val sockPath = Path.of(System.getProperty("seekprog.sock"));
    val sockAddr = UnixDomainSocketAddress.of(sockPath);
    val socketChannel = SocketChannel.open(StandardProtocolFamily.UNIX);
    socketChannel.connect(sockAddr);

    applet.frameRate(1e+9f + 1);
    val handlePre = new HandlePre(applet, targetFrameCount, socketChannel);
    applet.registerMethod("pre", handlePre);
  }
}

class HandlePre(
    applet: PApplet,
    targetFrameCount: Int,
    socketChannel: SocketChannel
) {
  var gBak: Option[PGraphics] = None;
  var onTarget = false;

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

    if (this.onTarget && this.applet.frameCount % 60 == 0) {
      socketChannel.write(
        RuntimeEvent
          .OnUpdateLocation(this.applet.frameCount.toDouble / 60)
          .toBytes()
      )
    }
  }
}
