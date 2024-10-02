fun createAppender(start: String): (String) -> String {
    // Return a function that takes in a string and returns a string
    return { s: String -> "$start $s" }
}

fun averageLength(list: List<String>): Int {
    val sequence = list.asSequence()
    val trace = createAppender("[averageLength]")
    println(trace("starting my complicated processing"))
    return sequence.map { string ->
        println(trace("transforming $string -> $string.size"))
        string.length
    }.fold(0) {acc, next ->
        println(trace("calculating sum $acc"))
        acc + next
    }.div(list.size)
}

val average = averageLength(listOf("test", "value", "hello"))

println(average)