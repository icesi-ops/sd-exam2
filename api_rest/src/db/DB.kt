package zero.network.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import zero.network.db.dao.Movies
import zero.network.db.model.Movie

object DB {
    var isTestMode: Boolean = false
    val db: Database by lazy {
        if (!isTestMode)
            Database.connect(
                "jdbc:postgresql://${DBInfo.hostDB}:${DBInfo.portDB}/${DBInfo.nameDB}?sslmode=disable",
                driver = "org.postgresql.Driver", user = DBInfo.userDB
            )
        else Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    }

    fun save(movie: Movie) = transaction(db) {
        Movies.insert {
            it[name] = movie.name
            it[age] = movie.year
        }
    }
}