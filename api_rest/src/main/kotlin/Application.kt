package zero.network

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.utils.io.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import zero.network.db.DB
import zero.network.db.dao.Movies
import zero.network.db.model.DefaultMovieList

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DB.isTestMode = testing
    val exist = transaction(DB.db) { Movies.exists() }

    if (!exist) {
        transaction(DB.db) {
            SchemaUtils.create(Movies)
        }
        DefaultMovieList.forEach(DB::save)
    }

    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/") {
            call.respond(APIInfo())
        }
        route("/movie"){
            get {
                val movies = transaction(DB.db) {
                    Movies.selectAll().map (Movies::toMovie)
                }
                call.respond(movies)
            }
        }
    }
}
