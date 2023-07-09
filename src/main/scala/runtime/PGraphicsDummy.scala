package net.kgtkr.seekprog.runtime;

import processing.core.PGraphics

class PGraphicsDummy extends PGraphics {
  override def beginDraw(): Unit = {
    this.vertexCount = 0
  }

  override def rectImpl(x1: Float, y11: Float, x21: Float, y21: Float) = {}
}
