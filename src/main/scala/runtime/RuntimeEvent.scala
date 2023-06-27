package net.kgtkr.seekprog.runtime;

import java.nio.ByteBuffer
import io.circe._, io.circe.generic.semiauto._, io.circe.parser._,
  io.circe.syntax._
import java.nio.charset.StandardCharsets

object RuntimeEvent {
  implicit val encoder: Encoder[RuntimeEvent] = deriveEncoder
  implicit val decoder: Decoder[RuntimeEvent] = deriveDecoder

  def fromBytes(bytes: ByteBuffer): RuntimeEvent = {
    decode[RuntimeEvent](
      StandardCharsets.UTF_8.decode(bytes).toString()
    ).right.get
  }

}

enum RuntimeEvent {
  case OnTargetFrameCount;
  case OnUpdateLocation(frameCount: Int, events: List[List[EventWrapper]]);

  def toBytes(): ByteBuffer = {
    ByteBuffer.wrap(
      (this.asJson.noSpaces + "\n").getBytes(StandardCharsets.UTF_8)
    )
  }
}
