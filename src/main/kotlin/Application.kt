package zero.network

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import zero.network.db.DB
import zero.network.db.dao.Movies
import zero.network.db.model.DefaultMovieList
import zero.network.routes.apiRoutes

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
        json(Json { ignoreUnknownKeys = true })
    }
    routing {
        route("/api") {
            get {
                call.respond(APIInfo)
            }
            apiRoutes()
        }
    }
}

