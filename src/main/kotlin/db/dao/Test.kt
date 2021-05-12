package zero.network.db.dao

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import zero.network.db.dao.Movies.autoIncrement

object Test : Table() {
    val id: Column<Long> = long("id").autoIncrement()
    val value: Column<String> = varchar("value", 255)

    override val primaryKey: PrimaryKey = PrimaryKey(Movies.id, name = "PK_TEST_ID")
}