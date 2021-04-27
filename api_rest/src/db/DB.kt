package zero.network.db

import org.jetbrains.exposed.sql.Database

object DB {
    val db: Database by lazy { Database.connect(
        "jdbc:postgresql://${DBInfo.hostDB}:${DBInfo.portDB}/${DBInfo.nameDB}?sslmode=disable",
        driver = "org.postgresql.Driver", user = DBInfo.userDB
    ) }
}