package zero.network

import kotlinx.serialization.Serializable
import zero.network.db.DB

@Serializable
data class APIInfo (
    val version: String = "0.0.1",
    val message: String = "Welcome to Movies API",
    val inTestMode: Boolean = DB.isTestMode,
    val isOnline: Boolean = online,
    val options: List<Option> = listOf(
        Option("API INFO", "GET", "/", "Show this info"),
        Option("list movies", "GET", "/movie", "List all movies on the DB")
    )
)

@Serializable
data class Option(val name: String, val methodType: String, val uri: String, val description: String)