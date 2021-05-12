package zero.network

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import zero.network.db.model.DefaultMovieList
import zero.network.db.model.Movie
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {


    private inline fun withTestEngine(crossinline test: TestApplicationEngine.() -> Unit) =
        withTestApplication({ module(testing = true) }) {
            test()
        }

    @Test
    fun apiInfoSerialization() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/api").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(APIInfo, Json.decodeFromString<Info>(response.content?:"{}"))
            }
        }
    }
    @Test
    fun movieGetErrorID() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/api/movie/a").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing or malformed id",
                    Json.decodeFromString<Map<String,String>>(response.content ?: "{}")["error"]
                )
            }
        }
    }
    @Test
    fun movieGetErrorMovieDoesntExist() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/api/movie/-1").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("The Movie with id=-1 doesn't exist",
                    Json.decodeFromString<Map<String,String>>(response.content ?: "{}")["error"]
                )
            }
        }
    }
    @Test
    fun createMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Post, "/api/movie"){
                addHeader("Content-Type", "application/json")
                setBody("""{"name":"limitless","year":2011,"id":null}""")
            }.apply {
                println(response.content)
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(Movie(name="limitless",year=2011), Json.decodeFromString<Map<String,List<Movie>>>(response.content?:"{}")["added"]!!.first().copy(id=null))
            }
        }
    }
    @Test
    fun createErrorMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Post, "/api/movie"){
                addHeader("Content-Type", "application/json")
                setBody("""{"name":"limitless","year":2011,"id"=null}""")
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("invalid request body",
                    Json.decodeFromString<Map<String,String>>(response.content ?: "{}")["error"]
                )
            }
        }
    }
    @Test
    fun deleteMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/api/movie").apply {
                val movies = Json.decodeFromString<List<Movie>>(response.content ?: "[]")
                handleRequest(HttpMethod.Delete, "/api/movie/${movies.first().id}").apply {
                    println(response.content)
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertEquals(movies.first(),
                        Json.decodeFromString<Map<String,Movie>>(response.content ?: "{}")["deleted_movie"]
                    )
                }
            }
        }
    }
    @Test
    fun updateMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/api/movie").apply {
                val movies = Json.decodeFromString<List<Movie>>(response.content ?: "[]")
                handleRequest(HttpMethod.Post, "/api/movie/${movies.first().id}"){
                    addHeader("Content-Type", "application/json")
                    setBody("""{"name":"limitless","year":${movies.first().year},"id":null}""")
                }.apply {
                    println(response.content)
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertEquals(movies.first().copy(name="limitless"),
                        Json.decodeFromString<Movie>(response.content ?: "{}")
                    )
                }
            }
        }
    }
    @Test
    fun updateErrorIDMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Post, "/api/movie/a"){
                addHeader("Content-Type", "application/json")
                setBody("""{"name":"limitless","year":1999,"id":null}""")
            }.apply {

                assertEquals("Missing or malformed id",
                    Json.decodeFromString<Map<String,String>>(response.content ?: "{}")["error"]
                )
            }
        }
    }
    @Test
    fun updateErrorBodyMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/api/movie").apply {
                val movies = Json.decodeFromString<List<Movie>>(response.content ?: "[]")
                handleRequest(HttpMethod.Post, "/api/movie/${movies.first().id}"){
                    addHeader("Content-Type", "application/json")
                    setBody("""{"name":"limitless","year":${movies.first().year},"id"=null}""")
                }.apply {
                    assertEquals(HttpStatusCode.BadRequest, response.status())
                    assertEquals("invalid request body",
                        Json.decodeFromString<Map<String,String>>(response.content ?: "{}")["error"]
                    )
                }
            }
        }
    }
    @Test
    fun deleteWithUnexistIDMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Delete, "/api/movie/-1").apply {
                println(response.content)
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(null,
                    Json.decodeFromString<Map<String,Movie?>>(response.content ?: "{}")["deleted_movie"]
                )
            }
        }
    }
    @Test
    fun deleteWithBadIDMovieTest() {
        withTestEngine {
            handleRequest(HttpMethod.Delete, "/api/movie/aaaa").apply {
                println(response.content)
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing or malformed id",
                    Json.decodeFromString<Map<String,String>>(response.content ?: "{}")["error"]
                )
            }
        }
    }

    @Test
    fun healthTest() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/api/health").apply {
                println(response.content)
              assertEquals(response.content, "{\"status_DB\":\"Online\"}")
            }
        }
    }
}
