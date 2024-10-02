import java.io.File
import kotlin.math.abs

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day10.txt".asFile().readLines()

enum class Instruction {
    NOOP,
    ADDX;

    companion object {
        fun fromString(value: String) = when (value) {
            "noop" -> NOOP
            "addx" -> ADDX
            else -> NOOP
        }
    }
}

data class Command(val instruction: Instruction, val argument: Int? = null) {
    companion object {
        fun fromString(value: String): Command {
            val parts = value.split(" ")

            return if (parts.size > 1) {
                Command(
                    instruction = Instruction.fromString(parts[0]),
                    argument = parts[1].toInt()
                )
            } else {
                Command(instruction = Instruction.fromString(parts[0]))
            }
        }
    }
}

val instructions = input.map { Command.fromString(it) }

val results = instructions
    .fold(Pair(emptyList<Int>(), "NaN".toIntOrNull())) { acc, command ->
        val lastTotal = acc.first.run {
            if (this.isEmpty()) {
                return@run 1
            } else {
                return@run this.last()
            }
        }

        val update = if (acc.second != null) {
            acc.first + listOf(lastTotal, (acc.second!! + lastTotal))
        } else {
            acc.first + lastTotal
        }

        val storedValue = if (command.instruction == Instruction.ADDX) {
            command.argument
        } else {
            null
        }

        Pair(update, storedValue)
    }.run {
        // TODO: Could DRY this up
        val lastValue = this.first.last()

        if (this.second == null) {
            return@run this.first + lastValue
        } else {
            return@run this.first + listOf(lastValue, this.second!! + lastValue)
        }
    }

// part 1
println((20..results.size step 40).map { cycle ->
    cycle * results[cycle - 1]
}.sum())

// part 2
results
    .mapIndexed { pixel, value ->
        (abs(value - (pixel%40)) <= 1).run {
            if (this) {
                "#"
            } else {
                "."
            }
        }
    }
    .windowed(40, 40)
    .forEach { println(it.joinToString("")) }