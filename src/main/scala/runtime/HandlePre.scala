package net.kgtkr.seekprog.runtime;

import processing.core.PApplet;
import processing.core.PGraphics

class HandlePre(applet: PApplet, targetFrameCount: Int) {
  var gBak: Option[PGraphics] = None;

  def pre() = {
    if (this.applet.frameCount == 1) {
      this.gBak = Some(this.applet.g);
      this.applet.g = new PGraphics();
    }

    if (this.applet.frameCount == this.targetFrameCount) {
      this.applet.g.dispose();
      this.applet.g = this.gBak.get;
      new OnHandlePre().onTargetFrameCount(this.applet);
    }
  }
}
