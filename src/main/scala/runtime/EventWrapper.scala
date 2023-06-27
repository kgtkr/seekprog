package net.kgtkr.seekprog.runtime;

import processing.event.MouseEvent
import processing.event.KeyEvent
import io.circe._, io.circe.generic.semiauto._, io.circe.parser._,
  io.circe.syntax._

/*
MouseEvent(Object nativeObject,
                    long millis, int action, int modifiers,
                    int x, int y, int button, int count)
 */

object MouseEventWrapper {
  implicit val encoder: Encoder[MouseEventWrapper] = deriveEncoder
  implicit val decoder: Decoder[MouseEventWrapper] = deriveDecoder

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
  implicit val encoder: Encoder[KeyEventWrapper] = deriveEncoder
  implicit val decoder: Decoder[KeyEventWrapper] = deriveDecoder

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

object EventWrapper {
  implicit val encoder: Encoder[EventWrapper] = deriveEncoder
  implicit val decoder: Decoder[EventWrapper] = deriveDecoder
}

enum EventWrapper {
  case Mouse(e: MouseEventWrapper);
  case Key(e: KeyEventWrapper);
}
