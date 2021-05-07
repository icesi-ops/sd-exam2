package zero.network.db.model

private val list = mutableListOf<Movie>()
private operator fun Movie.unaryPlus() = list.add(this)

val DefaultMovieList: List<Movie> = list.apply {
        +Movie(name = "Big Hero 6", year= 2014)
        +Movie(name = "Spirited Away", year= 2001)
        +Movie(name = "Laputa: Castle in the Sky", year= 1986)
        +Movie(name = "Kaguya-hime no Monogatari", year= 2013)
        +Movie(name = "Princess Mononoke", year= 1997)
        +Movie(name = "Iron Man", year= 2008)
        +Movie(name = "Iron Man 2", year= 2010)
        +Movie(name = "Iron Man 3", year= 2013)
        list.sortBy { it.name }
}