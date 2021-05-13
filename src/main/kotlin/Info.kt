package zero.network

import kotlinx.serialization.Serializable
import zero.network.db.DB

@Serializable
open class Info(
    val version: String,
    val message: String,
    val inTestMode: Boolean,
    val options: List<Option>
){

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + inTestMode.hashCode()
        result = 31 * result + options.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Info

        if (version != other.version) return false
        if (message != other.message) return false
        if (inTestMode != other.inTestMode) return false
        if (options != other.options) return false

        return true
    }
}


val APIInfo by lazy {
    Info(
        version = "0.0.1",
        message = "Welcome to Movies API",
        inTestMode = DB.isTestMode,
        options = listOf(
            Option("API INFO", "GET", "/api", "Show this info"),
            Option("list movies", "GET", "/api/movie", "List all movies on the DB"),
            Option("get movie", "GET", "/api/movie/{ID}", "return the movie with the specific ID"),
            Option("create movie", "POST", "/api/movie", "add the movie or list of movies to the DB and return it"),
            Option("update movie", "POST", "/api/movie/{ID}", "update the specified movie and return it"),
            Option("delete movie", "DELETE", "/api/movie/{ID}", "delete the specified movie and return it"),
        )
    )
}


@Serializable
data class Option(val name: String, val methodType: String, val uri: String, val description: String)