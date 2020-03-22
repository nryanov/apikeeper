package apikeeper

import apikeeper.http.internal.{EntityTypeFilter, Filter, NameFilter}
import cats.syntax.show._
import cats.syntax.functor._
import io.circe.{Decoder, Encoder}
import io.circe.syntax._
import io.circe.generic.auto._
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.{Codec, DecodeResult}
import apikeeper.model.Id

import scala.util.{Failure, Success, Try}

package object http {
  def decode(s: String): DecodeResult[Id] = Try(Id(s)) match {
    case Success(v) => DecodeResult.Value(v)
    case Failure(f) => DecodeResult.Error(s, f)
  }
  def encode(id: Id): String = id.show

  implicit val idCodec: Codec[Id, TextPlain, String] = Codec.stringPlainCodecUtf8.mapDecode(decode)(encode)

  implicit val encodeEvent: Encoder[Filter] = Encoder.instance {
    case nameFilter: NameFilter             => nameFilter.asJson
    case entityTypeFilter: EntityTypeFilter => entityTypeFilter.asJson
  }

  implicit val decodeEvent: Decoder[Filter] =
    List[Decoder[Filter]](
      Decoder[NameFilter].widen,
      Decoder[EntityTypeFilter].widen
    ).reduceLeft(_.or(_))
}
