package zero.network

open class DBInfo(
    val hostDB: String,
    val portDB: String,
    val nameDB: String,
    val userDB: String,
    val passwordDB: String
) {
    companion object : DBInfo(
        hostDB = DBInfo["DB_HOST", "localhost"],
        portDB = DBInfo["DB_PORT", "26257"],
        nameDB = DBInfo["DB_NAME", "movies"],
        userDB = DBInfo["DB_USER", "root"],
        passwordDB = DBInfo["DB_PASSWORD", "password"],
    ) {
        private operator fun get(variable: String, default: String): String = System.getenv(variable) ?: default
    }
}