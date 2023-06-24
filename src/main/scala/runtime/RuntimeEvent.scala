package net.kgtkr.seekprog.runtime;

import java.nio.ByteBuffer
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import java.nio.charset.StandardCharsets

object RuntimeEvent {
  def fromBytes(bytes: ByteBuffer): RuntimeEvent = {
    decode[RuntimeEvent](
      StandardCharsets.UTF_8.decode(bytes).toString()
    ).right.get
  }

}

enum RuntimeEvent {
  case OnTargetFrameCount;
  case OnUpdateLocation(time: Double);

  def toBytes(): ByteBuffer = {
    ByteBuffer.wrap(
      (this.asJson.noSpaces + "\n").getBytes(StandardCharsets.UTF_8)
    )
  }
}
