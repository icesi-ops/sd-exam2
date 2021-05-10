package zero.network.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import zero.network.db.DB
import zero.network.db.dao.Movies
import zero.network.db.model.Movie

fun Route.apiRoutes() {
    route("/movie") {
        movieGet()
        post {
            val movies = call.receive<Array<Movie>>()
            movies.forEach(DB::save)
        }
    }
}

private fun Route.movieGet() {
    get {
        val movies = transaction(DB.db) {
            Movies.selectAll().map(Movies::toMovie)
        }
        call.respond(movies)
    }
    get("{id}") {
        val id: Long = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(
            message = mapOf("error" to "Missing or malformed id"),
            status = HttpStatusCode.BadRequest
        )
        val movie = transaction(DB.db) {
            Movies.select { Movies.id eq id }.firstOrNull()?.let {
                Movies.toMovie(it)
            }
        } ?: return@get call.respond(
            message = mapOf("error" to "The Movie with id=$id doesn't exist"),
            status = HttpStatusCode.NotFound
        )
        call.respond(movie)
    }
}