package zero.network.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import zero.network.db.DB
import zero.network.db.dao.Movies
import zero.network.db.model.Movie

fun Route.apiRoutes() {
    route("/movie") {
        movieGet()
        moviePost()
        delete("{id}") {
            val id: Long = call.parameters["id"]?.toLongOrNull()?: return@delete call.respond(
                message = mapOf("error" to "Missing or malformed id"),
                status = HttpStatusCode.BadRequest
            )
            val movie = transaction(DB.db) {
                val m = Movies.select { Movies.id eq id }.firstOrNull()?.let { Movies.toMovie(it) }
                if (m!=null && Movies.deleteWhere { Movies.id eq id } >= 1) m
                else null
            }
            call.respond(mapOf("deleted_movie" to movie))
        }
    }
}

private fun Route.moviePost() {
    post {
        val movie = try { call.receiveOrNull<Movie>() } catch (e: Exception) { null }?: return@post call.respond(
            status = HttpStatusCode.BadRequest,
            message = mapOf("error" to "invalid request body")
        )
        val result = movie.let(DB::save).resultedValues?.map(Movies::toMovie)
        call.respond(mapOf("added" to result))
    }
    post("{id}") {
        val id: Long = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(
            message = mapOf("error" to "Missing or malformed id"),
            status = HttpStatusCode.BadRequest
        )
        val movie = try { call.receiveOrNull<Movie>() } catch (e: Exception) { null } ?: return@post call.respond(
            status = HttpStatusCode.BadRequest,
            message = mapOf("error" to "invalid request body")
        )
        val updates = transaction(DB.db) {
            Movies.update({ Movies.id eq id }) {
                it[name] = movie.name
                it[age] = movie.year
            }
        }
        if (updates >= 1) call.respond(movie.copy(id = id))
        else call.respond(mapOf("error" to "fail to update"))
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