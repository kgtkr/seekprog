// generated
package net.kgtkr.seekprog.runtime;

import processing.awt.PGraphicsJava2D

class PGraphicsJava2DDummyImpl extends PGraphicsJava2D {

  override def clipImpl(arg0: Float, arg1: Float, arg2: Float, arg3: Float) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.clipImpl(arg0, arg1, arg2, arg3)
    }
  }

  override def blendModeImpl() = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.blendModeImpl()
    }
  }

  override def rectImpl(arg0: Float, arg1: Float, arg2: Float, arg3: Float) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.rectImpl(arg0, arg1, arg2, arg3)
    }
  }

  override def rectImpl(
      arg0: Float,
      arg1: Float,
      arg2: Float,
      arg3: Float,
      arg4: Float,
      arg5: Float,
      arg6: Float,
      arg7: Float
  ) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.rectImpl(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
    }
  }

  override def ellipseImpl(
      arg0: Float,
      arg1: Float,
      arg2: Float,
      arg3: Float
  ) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.ellipseImpl(arg0, arg1, arg2, arg3)
    }
  }

  override def arcImpl(
      arg0: Float,
      arg1: Float,
      arg2: Float,
      arg3: Float,
      arg4: Float,
      arg5: Float,
      arg6: Int
  ) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.arcImpl(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
    }
  }

  override def imageImpl(
      arg0: processing.core.PImage,
      arg1: Float,
      arg2: Float,
      arg3: Float,
      arg4: Float,
      arg5: Int,
      arg6: Int,
      arg7: Int,
      arg8: Int
  ) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.imageImpl(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
    }
  }

  override def textFontImpl(arg0: processing.core.PFont, arg1: Float) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.textFontImpl(arg0, arg1)
    }
  }

  override def textSizeImpl(arg0: Float) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.textSizeImpl(arg0)
    }
  }

  override def textLineAlignImpl(
      arg0: Array[Char],
      arg1: Int,
      arg2: Int,
      arg3: Float,
      arg4: Float
  ) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.textLineAlignImpl(arg0, arg1, arg2, arg3, arg4)
    }
  }

  override def textLineImpl(
      arg0: Array[Char],
      arg1: Int,
      arg2: Int,
      arg3: Float,
      arg4: Float
  ) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.textLineImpl(arg0, arg1, arg2, arg3, arg4)
    }
  }

  override def textCharImpl(arg0: Char, arg1: Float, arg2: Float) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.textCharImpl(arg0, arg1, arg2)
    }
  }

  override def textCharModelImpl(
      arg0: processing.core.PImage,
      arg1: Float,
      arg2: Float,
      arg3: Float,
      arg4: Float,
      arg5: Int,
      arg6: Int
  ) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.textCharModelImpl(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
    }
  }

  override def backgroundImpl(arg0: processing.core.PImage) = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.backgroundImpl(arg0)
    }
  }

  override def backgroundImpl() = {
    if (RuntimeMain.sketchHandler.onTarget) {
      super.backgroundImpl()
    }
  }
}
