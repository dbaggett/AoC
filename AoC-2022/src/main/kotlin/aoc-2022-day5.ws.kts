import java.io.File

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day5.txt".asFile().readLines()

val upperAlpha = ('A'..'Z').toSet()

// Game parsing hardcoded to input. Not the best, but works for me.
val rows = input
    .slice(0..7)
    .map {
        it
            .toList()
            .chunked(4)
            .map { chars -> chars.intersect(upperAlpha)}
    }.reversed()

val moves = input
    .slice(10 until input.size)
    .map { move ->
        move
            .replace(" ", "")
            .replace("move", "")
            .replace("from", "-")
            .replace("to", "-")
    }

val stackings = mapOf(
    1 to ArrayDeque<Char>(),
    2 to ArrayDeque<Char>(),
    3 to ArrayDeque<Char>(),
    4 to ArrayDeque<Char>(),
    5 to ArrayDeque<Char>(),
    6 to ArrayDeque<Char>(),
    7 to ArrayDeque<Char>(),
    8 to ArrayDeque<Char>(),
    9 to ArrayDeque<Char>()
)

fun List<Set<Char>>.stacked() = this.forEachIndexed { index, chars ->
    if (chars.isNotEmpty()) {
        stackings[(index + 1)]?.addLast(chars.first())
    }
}

fun init() {
    stackings.values.forEach { it.clear()}

    rows.forEach { row -> row.stacked() }
}

/**
 * Uhg, resorted to a side-effect based approach.
 */
fun makeMoves(shouldPreserveOrder: Boolean = false) {
    init()
    val tempStack = ArrayDeque<Char>()

    moves.forEach {  move ->
        val instructions = move.split("-")
        val number = instructions[0].toInt()
        val from = instructions[1].toInt()
        val to = instructions[2].toInt()

        for (i in 0 until number) {

            if (shouldPreserveOrder) {
                stackings[from]?.removeLast()?.let {
                    tempStack.addLast(it)
                }
            } else {
                stackings[from]?.removeLast()?.let {
                    stackings[to]?.addLast(it)
                }
            }
        }

        if (shouldPreserveOrder) {
            while (tempStack.isNotEmpty()) {
                stackings[to]?.addLast(tempStack.removeLast())
            }
        }
    }
}

fun topLevelCrates() = (1..9).fold("") { acc, index ->
    acc + stackings[index]?.removeLast()
}

// part 1
makeMoves()
println(topLevelCrates())

// part 2
// TPWCGNCCG
makeMoves(true)
println(topLevelCrates())