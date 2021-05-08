package zero.network

import kotlinx.serialization.Serializable
import zero.network.db.DB

@Serializable
data class APIInfo(
    val version: String = "0.0.1",
    val message: String = "Welcome to Movies API",
    val inTestMode: Boolean = DB.isTestMode,
    val options: List<Option> = listOf(
        Option("API INFO", "GET", "/api", "Show this info"),
        Option("list movies", "GET", "/api/movie", "List all movies on the DB"),
        Option("list movies", "GET", "/api/movie/{ID}", "return the movie with the specific ID")
    )
) {
    @Serializable
    data class Option(val name: String, val methodType: String, val uri: String, val description: String)
}