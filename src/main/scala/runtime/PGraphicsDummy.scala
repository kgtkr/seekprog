package net.kgtkr.seekprog.runtime;

import processing.core.PGraphics

class PGraphicsDummy extends PGraphics {
  override def beginDraw(): Unit = {
    this.vertexCount = 0
  }
}
