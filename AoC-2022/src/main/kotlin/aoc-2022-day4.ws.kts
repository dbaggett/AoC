import java.io.File

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day4.txt".asFile().readLines()

fun Pair<Int, Int>.range() = (this.first..this.second)

val assignments = input.map { combinedSections ->
    combinedSections
        .split(",")
        .flatMap { sections ->
            sections
                .split("-", limit = 2)
                .zipWithNext()
                .map { pair -> Pair(pair.first.toInt(), pair.second.toInt()).range() }
        }
}

fun IntRange.contains(range: IntRange, complete: Boolean = false): Boolean {
    val comparedSet = range.toSet()
    val targetSet = this.toSet()

    return if (complete) {
        val shorterSet = if (comparedSet.size < targetSet.size) {
            comparedSet
        } else {
            targetSet // this will cover the equal in size case too
        }

        comparedSet.intersect(targetSet).sum() == shorterSet.sum()
    } else {
        comparedSet.intersect(targetSet).isNotEmpty()
    }
}

// part 1
println(assignments.map { ranges -> ranges[0].contains(ranges[1], true) }.filter { it }.size)

// part 2
println(assignments.map { ranges -> ranges[0].contains(ranges[1]) }.filter { it }.size)