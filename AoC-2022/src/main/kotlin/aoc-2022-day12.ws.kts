import java.io.File
import java.util.PriorityQueue
import kotlin.math.abs

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day12.txt".asFile().readLines()
val lowerAlphabetAndOrdinal = buildMap {
    ('a'..'z').toSet().mapIndexed { index, letter ->
        put(letter, index + 1)
    }
}

var startNode: Pair<Int, Int>? = null
var endNode: Pair<Int, Int>? = null
var lowestElevationNodes: MutableSet<Pair<Int, Int>> = mutableSetOf()

val nodes = input.mapIndexed { rowIndex, row ->
    row.mapIndexed { columnIndex, letter ->
        val position = Pair(rowIndex, columnIndex)

        if (letter == 'a') lowestElevationNodes.add(position)

        if (letter == 'S') {
            startNode = position
            lowestElevationNodes.add(position)
        }

        if (letter == 'E') endNode = position

        Triple(
            first = letter,
            second = position,
            third = lowerAlphabetAndOrdinal[letter].run {
                this
                    ?: if (letter == 'S') {
                        0
                    } else {
                        26
                    }
            }
        )
    }
}

val leftBounds = 0
val topBounds = 0
val rightBounds = nodes.first().size - 1
val bottomBounds = nodes.size - 1

fun Pair<Int, Int>.toNode() = nodes[this.first][this.second]

fun Triple<Char, Pair<Int, Int>, Int>.accessibleNeighbors(
    reversed: Boolean = false
): List<Triple<Char, Pair<Int, Int>, Int>> {
    val row = this.second.first
    val column = this.second.second

    val adjacentNeighbors = buildSet {
        if (column > leftBounds) add(Pair(row, column - 1).toNode())
        if (column < rightBounds) add(Pair(row, column + 1).toNode())
        if (row > topBounds) add(Pair(row - 1, column).toNode())
        if (row < bottomBounds) add(Pair(row + 1, column).toNode())
    }

    return adjacentNeighbors.filter { potentialNeighbor ->
        if (reversed) {
            (this.third - potentialNeighbor.third) <= 1
        } else {
            (potentialNeighbor.third - this.third) <= 1
        }
    }
}

fun Triple<Char, Pair<Int, Int>, Int>.reprioritize(f: Int, g: Int) =
    Triple(f, g, this)

fun Pair<Int, Int>.manhattanDistance() =
    abs(this.first - endNode!!.first) + abs(this.second - endNode!!.second)

fun shortestPathBetween(
    startNode: Pair<Int, Int>,
    endNodes: Set<Pair<Int, Int>>
): Set<Pair<Int, Int>> {
    // open set
    val queue = PriorityQueue<Triple<Int, Int, Triple<Char, Pair<Int, Int>, Int>>>(
        compareBy { it.first }
    )

    // closed set
    val inspected = mutableMapOf<Pair<Int, Int>, Int>()

    val parents = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>().apply {
        put(startNode, startNode)
    }

    val reversed = (startNode == endNode)

    queue.add(Triple(0, 0, startNode.toNode()))

    while (queue.isNotEmpty()) {
        val currentNode = queue.poll()

        if (endNodes.contains(currentNode.third.second)) {
            var node: Pair<Int, Int>? = currentNode.third.second
            val path = mutableSetOf<Pair<Int, Int>>()

            while (node != startNode) {
                path.add(node!!)
                node = parents[node]
            }

            return path.reversed().toSet()
        }

        currentNode.third.accessibleNeighbors(reversed).forEach { neighbor ->
            val tentativeG = currentNode.second + 1

            if (inspected[neighbor.second] == null ||
                (inspected[neighbor.second] ?: Int.MAX_VALUE) > tentativeG
            ) {
                inspected[neighbor.second] = tentativeG
                val heuristic = if (reversed) {
                    0
                } else {
                    neighbor.second.manhattanDistance()
                }

                parents[neighbor.second] = currentNode.third.second

                queue.add(neighbor.reprioritize(tentativeG + heuristic, tentativeG))
            }
        }
    }

    return emptySet()
}

// part 1
println(shortestPathBetween(startNode!!, setOf(endNode!!)).size)

// part 2
println(shortestPathBetween(endNode!!, lowestElevationNodes).size)