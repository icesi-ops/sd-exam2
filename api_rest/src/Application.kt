package zero.network

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import zero.network.db.DB
import zero.network.db.dao.Movies
import zero.network.db.model.DefaultMovieList
import zero.network.util.minutes
import java.lang.Thread.sleep
import kotlin.concurrent.thread

var online = false

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val setupDB =  {
        DB.isTestMode = testing
        if (!testing) sleep(2.5.minutes)

        val exist = transaction(DB.db) { Movies.exists() }

        if (!exist) {
            transaction(DB.db) {
                SchemaUtils.create(Movies)
            }
            DefaultMovieList.forEach(DB::save)
        }
        online = true
    }

    if (!testing) thread(block = setupDB)
    else setupDB()

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respond(APIInfo())
        }
        get("/movie"){
            if (!online) return@get call.respond(mapOf("state" to "offline"))
            val movies = transaction(DB.db) {
                Movies.selectAll().map (Movies::toMovie)
            }
            call.respond(movies)
        }
    }
}
