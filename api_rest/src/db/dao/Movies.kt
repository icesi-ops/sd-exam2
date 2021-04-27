package zero.network.db.dao

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Movies: Table(){
    val id: Column<Int> = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val age = integer("age")
    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "PK_USER_ID")
}