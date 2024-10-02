import java.io.File

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day3.txt".asFile().readLines()

val lowerUpperAlphabet = (('a'..'z') + ('A'..'Z')).joinToString("")

fun String.splitEven() = Pair(
    this.substring(0, this.length / 2).toList(),
    this.substring(this.length / 2).toList()
)

fun Pair<List<Char>, List<Char>>.intersection() = this.first.intersect(this.second.toSet())

fun List<List<Char>>.intersection() = this.reduce { acc, chars -> acc.intersect(chars).toList()}

val intersectionTotal = input
    .flatMap { it.splitEven().intersection() }
    .sumOf { lowerUpperAlphabet.indexOf(it) + 1 }

println(intersectionTotal)

val badgeTotal = input
    .chunked(3)
    .map { group ->
        group.map { it.toList() }
    }
    .flatMap { it.intersection() }
    .sumOf { lowerUpperAlphabet.indexOf(it) + 1 }

println(badgeTotal)