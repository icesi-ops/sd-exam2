package zero.network

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.css.CSSBuilder
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.FlowOrMetaDataContent
import kotlinx.html.style
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import zero.network.db.DB
import zero.network.db.DBInfo
import zero.network.db.dao.Movies
import java.lang.Thread.sleep

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    sleep(120 * 1000)

    transaction(DB.db) {
        SchemaUtils.create(Movies)
        Movies.insert {
            it[name] = "Big hero 6"
            it[age] = 2015
        }
    }

    install(ContentNegotiation) {
        gson {}
    }

    routing {
        get("/") {
            call.respond(Welcome)
        }
    }
}


fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
