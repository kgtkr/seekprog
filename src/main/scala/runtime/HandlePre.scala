package net.kgtkr.seekprog.runtime;

import processing.core.PApplet;
import processing.core.PGraphics;

object HandlePre {
  def apply(applet: PApplet, targetFrameCount: Int) = {
    applet.frameRate(1e+9f + 1);
    val handlePre = new HandlePre(applet, targetFrameCount);
    applet.registerMethod("pre", handlePre);
  }
}

class HandlePre(applet: PApplet, targetFrameCount: Int) {
  var gBak: Option[PGraphics] = None;
  var onTarget = false;
  val onHandlePre = new OnHandlePre();

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
      this.onHandlePre.onTargetFrameCount(this.applet);
    }

    if (this.onTarget && this.applet.frameCount % 60 == 0) {
      this.onHandlePre.onUpdateLocation(this.applet.frameCount.toDouble / 60);
    }
  }
}
