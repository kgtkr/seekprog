package net.kgtkr.seekprog.runtime;

import processing.core.PApplet;
import processing.core.PGraphics

class HandlePre(applet: PApplet, targetFrameCount: Int) {
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
      new OnHandlePre().onTargetFrameCount(this.applet);
    }

    if (this.onTarget && this.applet.frameCount % 60 == 0) {
      new OnHandlePre().onUpdateLocation(this.applet.frameCount.toDouble / 60);
    }
  }
}
