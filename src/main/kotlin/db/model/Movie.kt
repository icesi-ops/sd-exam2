package zero.network.db.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(val id: Long? = null, val name: String, val year: Int)
