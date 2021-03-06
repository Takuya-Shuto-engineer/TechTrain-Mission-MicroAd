package domains

trait Service[E <: Entity[_], ID <: Identifier[_]] {
  def save(entity: E): Unit

  def find(id: ID): Option[E]

  def getList(): List[E]
}
