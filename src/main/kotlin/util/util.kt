package zero.network.util

import kotlin.math.roundToLong

val Int.seconds get() = this * 1000L
val Double.seconds get() = (this * 1000).roundToLong()
val Double.minutes get() = (this * 60).seconds