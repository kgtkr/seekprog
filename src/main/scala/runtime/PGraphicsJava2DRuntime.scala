package net.kgtkr.seekprog.runtime;

import processing.awt.PGraphicsJava2D
import processing.core.PSurface

class PGraphicsJava2DRuntime extends PGraphicsJava2D {
  override def createSurface(): PSurface = {
    this.surface = new PSurfaceAWTRuntime(this);
    this.surface
  }
}
