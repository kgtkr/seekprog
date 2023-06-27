package net.kgtkr.seekprog.runtime;

import processing.event.MouseEvent
import processing.event.KeyEvent

/*
MouseEvent(Object nativeObject,
                    long millis, int action, int modifiers,
                    int x, int y, int button, int count)
 */

object MouseEventWrapper {
  def fromPde(e: MouseEvent): MouseEventWrapper = {
    MouseEventWrapper(
      e.getMillis(),
      e.getAction(),
      e.getModifiers(),
      e.getX(),
      e.getY(),
      e.getButton(),
      e.getCount()
    )
  }
}

case class MouseEventWrapper(
    millis: Long,
    action: Int,
    modifiers: Int,
    x: Int,
    y: Int,
    button: Int,
    count: Int
) {
  def toPde(): MouseEvent = {
    new MouseEvent(
      null,
      millis,
      action,
      modifiers,
      x,
      y,
      button,
      count
    )
  }
}

/*
public KeyEvent(Object nativeObject,
                  long millis, int action, int modifiers,
                  char key, int keyCode, boolean isAutoRepeat)
 */

object KeyEventWrapper {
  def fromPde(e: KeyEvent): KeyEventWrapper = {
    KeyEventWrapper(
      e.getMillis(),
      e.getAction(),
      e.getModifiers(),
      e.getKey(),
      e.getKeyCode(),
      e.isAutoRepeat()
    )
  }
}

case class KeyEventWrapper(
    millis: Long,
    action: Int,
    modifiers: Int,
    key: Char,
    keyCode: Int,
    isAutoRepeat: Boolean
) {
  def toPde(): KeyEvent = {
    new KeyEvent(
      null,
      millis,
      action,
      modifiers,
      key,
      keyCode,
      isAutoRepeat
    )
  }
}
