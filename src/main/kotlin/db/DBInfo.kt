package zero.network.db

object DBInfo {
    val hostDB by lazy { DBInfo["DB_HOST", "localhost"] }
    val portDB by lazy { DBInfo["DB_PORT", "26257"] }
    val nameDB by lazy { DBInfo["DB_NAME", "movies"] }
    val userDB by lazy { DBInfo["DB_USER", "root"] }
    val passwordDB by lazy { DBInfo["DB_PASSWORD", "password"] }
    private operator fun get(variable: String, default: String): String = System.getenv(variable) ?: default

}