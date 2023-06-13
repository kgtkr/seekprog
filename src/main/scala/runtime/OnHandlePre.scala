package net.kgtkr.seekprog.runtime;

import processing.core.PApplet;

class OnHandlePre {
  def onTargetFrameCount(applet: PApplet) = {
    println("onTargetFrameCount");
  }
}
