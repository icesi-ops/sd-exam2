package zero.network

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
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
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(APIInfo(), Json.decodeFromString(response.content?:"{}"))
            }
        }
    }

    @Test
    fun innerDBTest() {
        withTestEngine {
            handleRequest(HttpMethod.Get, "/movie").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(DefaultMovieList, Json.decodeFromString<List<Movie>>(response.content?:"[]").map {
                    it.copy(id = null)
                })
            }
        }
    }
}
