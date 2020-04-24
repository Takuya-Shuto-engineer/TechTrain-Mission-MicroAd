package domains
import domains.Identifier

trait Entity[ID <: Identifier[_]] { // IDはIdentifierのサブ型もしくは同一型（Identityを示すもの）
  val id: ID

  override def equals(obj: Any): Boolean = obj match {
    case other: Entity[_] => this.id == other.id // Entity型の場合にはIDを比較する
    case _ => false // 型が違う場合はそもそもFalse
  }
  override def hashCode: Int = id.hashCode
}