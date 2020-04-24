package domains
import play.api.db.Database

trait Repository[ID <: Identifier[_], E <: Entity[ID]] {
  def resolve(id: ID): Option[E]
  def store(entity: E): Unit
  def list(): List[E]
}

