import java.io.File
import kotlin.math.abs

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day9.txt".asFile().readLines()
val instructions = input.flatMap { instruction ->
    val parts = instruction.split(" ")

    when (parts[0]) {
        "U" -> List(parts[1].toInt()) { Direction.UP }
        "D" -> List(parts[1].toInt()) { Direction.DOWN }
        "L" -> List(parts[1].toInt()) { Direction.LEFT }
        "R" -> List(parts[1].toInt()) { Direction.RIGHT }
        else -> List(parts[1].toInt()) { Direction.DOWN }// we're going down if this happens
    }
}

enum class Movement {
    VERTICAL,
    HORIZONTAL
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

data class RelativeTargetLocation(
    val verticalDirection: Direction? = null,
    val horizontalDirection: Direction? = null,
    val isAdjacent: Boolean = false
)

fun Pair<Int, Int>.move(direction: Direction) = when (direction) {
    Direction.UP -> Pair(this.first + 1, this.second)
    Direction.DOWN -> Pair(this.first - 1, this.second)
    Direction.LEFT -> Pair(this.first, this.second - 1)
    Direction.RIGHT -> Pair(this.first, this.second + 1)
}

fun Int.applyDirection(direction: Direction) = when (direction) {
    Direction.UP, Direction.RIGHT -> this + 1
    Direction.DOWN, Direction.LEFT -> this - 1
}

fun Pair<Int, Int>.moveTo(relativeDirection: RelativeTargetLocation) = when (relativeDirection.isAdjacent) {
    true -> this
    false -> Pair(
        relativeDirection.verticalDirection.run {
            if (this == null) {
                return@run this@moveTo.first
            } else {
                return@run this@moveTo.first.applyDirection(this)
            }
        },
        relativeDirection.horizontalDirection.run {
            if (this == null) {
                return@run this@moveTo.second
            } else {
                return@run this@moveTo.second.applyDirection(this)
            }
        }
    )
}

fun directionOrNull(relativeMovement: Movement, target: Int, reference: Int) = when (relativeMovement) {
    Movement.VERTICAL -> when {
        target > reference -> Direction.UP
        target < reference -> Direction.DOWN
        else -> null
    }
    Movement.HORIZONTAL -> when {
        target < reference -> Direction.LEFT
        target > reference -> Direction.RIGHT
        else -> null
    }
}

fun Pair<Int, Int>.relativeLocation(follower: Pair<Int, Int>) = RelativeTargetLocation(
    verticalDirection = directionOrNull(Movement.VERTICAL, this.first, follower.first),
    horizontalDirection = directionOrNull(Movement.HORIZONTAL, this.second, follower.second),
    isAdjacent = (abs(this.first - follower.first) <= 1
        && abs(this.second - follower.second) <= 1)
)

// part 1
val uniqueTailLocations = instructions.fold(Pair(listOf(Pair(0, 0)), Pair(0, 0))) { history, direction ->
    val headLocation = history.second.move(direction)
    val tailLocation = history.first.last()
    val relativeLocation = headLocation.relativeLocation(tailLocation)

    Pair((history.first + tailLocation.moveTo(relativeLocation)), headLocation)
}.first.toSet()

println(uniqueTailLocations.size)

// part 2
val uniqueTailLocationsMulti = instructions.fold(
    Pair(
        listOf(Pair(0, 0)),
        List(10) { Pair(0, 0) }
    )
) { history, direction ->
    val headLocation = history.second.first().move(direction)
    val chain = history.second.takeLast(history.second.size - 1).fold(listOf(headLocation)) { chain, localTailLocation ->
        val localHeadLocation = chain.last()
        val relativeLocation = localHeadLocation.relativeLocation(localTailLocation)

        chain + localTailLocation.moveTo(relativeLocation)
    }

    val visitedTailLocations = if (chain.size >= 10) {
        history.first + chain.last()
    } else {
        val localHeadLocation = chain.last()
        val ultimateTailLocation = history.first.first()
        history.first + (ultimateTailLocation.moveTo(localHeadLocation.relativeLocation(ultimateTailLocation)))
    }

    Pair(visitedTailLocations, chain)
}.first.toSet()

println(uniqueTailLocationsMulti.size)