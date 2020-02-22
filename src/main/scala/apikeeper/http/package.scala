package apikeeper

import apikeeper.model.Id
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.{Codec, DecodeResult}

import scala.util.{Failure, Success, Try}

package object http {
  def decode(s: String): DecodeResult[Id] = Try(Id(s)) match {
    case Success(v) => DecodeResult.Value(v)
    case Failure(f) => DecodeResult.Error(s, f)
  }
  def encode(id: Id): String = id.value.toString

  implicit val idCodec: Codec[Id, TextPlain, String] = Codec.stringPlainCodecUtf8.mapDecode(decode)(encode)
}
