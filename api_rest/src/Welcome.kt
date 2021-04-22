package zero.network

open class Welcome private constructor(){
    val version = "0.0.1"
    val message = "Welcome to Movies API"
    val options = listOf(
        Option("list movies", "GET", "/movies")
    )
    companion object: Welcome()
}

data class Option(val name: String, val methodType: String, val uri: String)