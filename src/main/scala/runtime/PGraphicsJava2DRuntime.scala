package net.kgtkr.seekprog.runtime;

import processing.awt.PGraphicsJava2D
import processing.core.PSurface

class PGraphicsJava2DRuntime extends PGraphicsJava2D {
  override def createSurface(): PSurface = {
    this.surface = new PSurfaceAWTRuntime(this);
    this.surface
  }

  override def beginDraw(): Unit = {
    super.beginDraw();
    if (RuntimeMain.handlePre.onTarget) {
      this.vertexCount = 0
    }

  }

  override def endDraw(): Unit = {
    if (RuntimeMain.handlePre.onTarget) {
      super.endDraw()
    }
  }

  override def rectImpl(x1: Float, y11: Float, x21: Float, y21: Float) = {
    if (RuntimeMain.handlePre.onTarget) {
      super.rectImpl(x1, y11, x21, y21)
    }
  }
}
