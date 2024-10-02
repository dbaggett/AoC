import java.io.File

/**
 * A = Rock = 1pt
 * B = Paper = 2pt
 * C = Scissors = 3pt
 *
 * X = Lose
 * Y = Draw
 * Z = Win
 *
 * Lose = 0pt
 * Draw = 3pt
 * Win = 6pt
 */

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day2.txt".asFile().readLines()

enum class Decision(val value: Int) {
    LOSE(0),
    DRAW(3),
    WIN(6)
}

enum class Shape(val value: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3)
}

fun Shape.kryptonite() = when (this) {
    Shape.ROCK -> Shape.PAPER
    Shape.PAPER -> Shape.SCISSORS
    Shape.SCISSORS -> Shape.ROCK
}

fun Shape.unworthy() = when (this) {
    Shape.ROCK -> Shape.SCISSORS
    Shape.PAPER -> Shape.ROCK
    Shape.SCISSORS -> Shape.PAPER
}

fun String.shape(): Shape? = when (this) {
    "A", "X" -> Shape.ROCK
    "B", "Y" -> Shape.PAPER
    "C", "Z" -> Shape.SCISSORS
    else -> null // this would jack up the round
}

fun Pair<Shape, Shape>.points(): Int {
    val roundPoints = when (this.second) {
        Shape.ROCK -> when (this.first) {
            Shape.ROCK -> Decision.DRAW.value
            Shape.PAPER -> Decision.LOSE.value
            Shape.SCISSORS -> Decision.WIN.value
        }
        Shape.PAPER -> when (this.first) {
            Shape.ROCK -> Decision.WIN.value
            Shape.PAPER -> Decision.DRAW.value
            Shape.SCISSORS -> Decision.LOSE.value
        }
        Shape.SCISSORS -> when (this.first) {
            Shape.ROCK -> Decision.LOSE.value
            Shape.PAPER -> Decision.WIN.value
            Shape.SCISSORS -> Decision.DRAW.value
        }
    }

    return roundPoints + second.value
}

val rounds = input.flatMap { round ->
    round
        .split(" ")
        .mapNotNull { it.shape() } // yolo
        .zipWithNext()
}

// points, part 1
println(rounds.sumOf { it.points() })

//points, part 2
println(
    rounds.map { round ->
        when (round.second) {
            Shape.ROCK -> Pair(round.first, round.first.unworthy())// X
            Shape.PAPER -> Pair(round.first, round.first)// Y
            Shape.SCISSORS -> Pair(round.first, round.first.kryptonite())// Z
        }
    }.sumOf { it.points() }
)