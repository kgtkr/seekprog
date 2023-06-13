package net.kgtkr.seekprog;

import processing.core.PApplet;

class HandlePre(applet: PApplet, targetFrameCount: Int) {
  def pre() = {
    if (this.applet.frameCount == this.targetFrameCount) {
      new OnHandlePre().onTargetFrameCount(this.applet);
    }
  }
}
