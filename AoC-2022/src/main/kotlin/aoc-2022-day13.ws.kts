import java.io.File

fun String.asFile(): File = File(this)

fun String.toActualList(): List<String> =
    this.replace("[", "")
        .replace("]", "")
        .split(", ")

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day13-test.txt"
    .asFile()
    .readLines()
    .filter { it.isNotEmpty() }
    .chunked(2)
    .map { packets ->
        Pair(
            packets[0].toActualList().map { it.toInt() },
            packets[1].toActualList().map { it.toInt() }
        )
    }

/**fun Pair<List<Int>, List<Int>>.withOrdered(): Triple<List<Int>, List<Int>, Boolean> {
    val leftPacket = this.first
    val rightPacket = this.second

    leftPacket.fol
}*/

println(input)

