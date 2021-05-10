package zero.network.db.dao

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import zero.network.db.model.Movie

object Movies: Table(){
    val id: Column<Long> = long("id").autoIncrement()
    val name = varchar("name", 255)
    val age = integer("age")
    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "PK_USER_ID")
    fun toMovie(row: ResultRow) = Movie(
        id = row[id],
        name = row[name],
        year = row[age]
    )
}