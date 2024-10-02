import java.io.File

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day8.txt".asFile().readLines()
val rows = input.map { it.map { it.toString().toInt() } }
val edgeCount = (rows.first().size * 2) + ((rows.size - 2) * 2)

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

fun Pair<Int, Int>.isTallestInThisDirection(direction: Direction): Boolean {
    val latitude = this.second
    val longitude = this.first
    val height = rows[longitude][latitude]

    return when (direction) {
        Direction.UP -> {
            buildList {
                ((longitude - 1)downTo 0).forEach {
                    add(rows[it][latitude])
                }
            }.fold(height) { acc, i -> if (acc > i) acc else acc + (i + 1) } == height
        }
        Direction.DOWN -> {
            buildList {
                ((longitude + 1) until rows.size).forEach {
                    add(rows[it][latitude])
                }
            }.fold(height) { acc, i -> if (acc > i) acc else acc + (i + 1) } == height
        }
        Direction.LEFT -> {
            buildList {
                ((latitude - 1)downTo 0).forEach {
                    add(rows[longitude][it])
                }
            }.fold(height) { acc, i -> if (acc > i) acc else acc + (i + 1) } == height
        }
        Direction.RIGHT -> {
            buildList {
                ((latitude + 1) until rows.first().size).forEach {
                    add(rows[longitude][it])
                }
            }.fold(height) { acc, i -> if (acc > i) acc else acc + (i + 1) } == height
        }
    }
}

fun Pair<Int, Int>.viewInThisDirection(direction: Direction): Int {
    val latitude = this.second
    val longitude = this.first
    val height = rows[longitude][latitude]

    return when (direction) {
        Direction.UP -> {
            buildList {
                ((longitude - 1)downTo 0).forEach {
                    val currentHeight = rows[it][latitude]
                    add(currentHeight)

                    if (currentHeight >= height) {
                        return@buildList
                    }
                }
            }.size
        }
        Direction.DOWN -> {
            buildList {
                ((longitude + 1) until rows.size).forEach {
                    val currentHeight = rows[it][latitude]
                    add(currentHeight)

                    if (currentHeight >= height) {
                        return@buildList
                    }
                }
            }.size
        }
        Direction.LEFT -> {
            buildList {
                ((latitude - 1)downTo 0).forEach {
                    val currentHeight = rows[longitude][it]
                    add(currentHeight)

                    if (currentHeight >= height) {
                        return@buildList
                    }
                }
            }.size
        }
        Direction.RIGHT -> {
            buildList {
                ((latitude + 1) until rows.first().size).forEach {
                    val currentHeight = rows[longitude][it]
                    add(currentHeight)

                    if (currentHeight >= height) {
                        return@buildList
                    }
                }
            }.size
        }
    }
}

val giants = buildList {
    (1 until rows.size - 1).forEach { longitude ->
        (1 until rows.first().size - 1).forEach { latitude ->
            val location = Pair(longitude, latitude)
            if (enumValues<Direction>()
                .map { direction ->
                    location.isTallestInThisDirection(direction)
                }
                .any { it }) {
                add(location)
            }
        }
    }
}

val scores = buildList {
    (rows.indices).forEach { longitude ->
        (rows.first().indices).forEach { latitude ->
            val location = Pair(longitude, latitude)
            val score = enumValues<Direction>()
                .map { location.viewInThisDirection(it) }
                .reduce { acc, i -> acc * i }

            add(score)
        }
    }
}

// part 1
println(giants.size + edgeCount)

// part 2
println(scores.maxOrNull())