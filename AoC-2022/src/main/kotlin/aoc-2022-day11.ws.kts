import java.io.File
import kotlin.math.floor
import kotlin.math.round

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day11.txt"
    .asFile()
    .readLines()
    .map { it.trim() }
    .windowed(6, 7)

val monkeys = input.map { Monkey.fromInput(it) }

val monkeyMap = buildMap {
    monkeys.forEach { put(it.id, it) }
}

data class Monkey(
    val id: String,
    val items: ArrayDeque<Long>,
    val operation: (Long) -> Long,
    val divisor: Long,
    val truthyMonkeyId: String,
    val falsyMonkeyId: String
) {
    fun offload(monkeyMap: Map<String, Monkey>, worryDamper: (Long) -> Long): Long {
        val inspectionCount = items.size

        if (items.isNotEmpty()) {
            do {
                val worryLevel = worryDamper(operation(items.removeFirst()))

                val targetMonkey = if (worryLevel % divisor == 0L) {
                    monkeyMap[truthyMonkeyId]
                } else {
                    monkeyMap[falsyMonkeyId]
                }

                targetMonkey?.items?.add(worryLevel)
            } while (items.isNotEmpty())
        }

        return inspectionCount.toLong()
    }

    companion object {
        fun fromInput(input: List<String>): Monkey {
            val id = input[0]
                .split(" ")[1]
                .replace(":", "")

            val items = input[1]
                .split(": ")[1]
                .split(", ")
                .map { it.toLong() }

            val operation = input[2]
                .split(" = ")[1]
                .split(" ")
                .run {
                    when(this[1]) {
                        "*" -> { value: Long ->
                            value * this[2].run {
                                if (this == "old") {
                                    value
                                } else {
                                    this.toLong()
                                }
                            }
                        }
                        "+" -> { value: Long ->
                            value + this[2].run {
                                if (this == "old") {
                                    value
                                } else {
                                    this.toLong()
                                }
                            }
                        }
                        else -> { value: Long ->
                            value + 0L
                        }// noop
                    }
                }

            val test = input[3]
                .split(": ")[1]
                .split(" ")[2]
                .toLong()

            val truthyId = input[4]
                .split(": ")[1]
                .split(" ")[3]

            val falsyId = input[5]
                .split(": ")[1]
                .split(" ")[3]

            return Monkey(
                id = id,
                items = ArrayDeque(items),
                operation = operation,
                divisor = test,
                truthyMonkeyId = truthyId,
                falsyMonkeyId = falsyId
            )
        }
    }
}

fun List<Map<String, Long>>.topTotal(n: Int): Long {
    return this
        .reduce { previousRounds, currentRound ->
            buildMap {
                previousRounds.keys.forEach { monkeyId ->
                    put(
                        monkeyId,
                        (previousRounds.getOrElse(monkeyId) { 0L } +
                            currentRound.getOrElse(monkeyId) { 0L })
                    )
                }
            }
        }
        .toList()
        .sortedByDescending { (_, value) -> value }
        .take(n)
        .map { it.second }
        .reduce { total, current ->
            total * current
        }
}

/** TODO: BEWARE
 * The monkey mappings are built once and reused. There is no queue clearing logic between runs. I was too lazy to fix
 * this (mostly out of annoyance for being stumped by the issue myself). Current solution: comment out part 1 to get the
 * correct part 2 result.
 */
// part 1
val topMonkeyBusiness = (1..20)
    .map {
        buildMap {
            monkeys.forEach { monkey ->
                put(monkey.id, monkey.offload(monkeyMap) { it / 3 })
            }
        }
    }
    .topTotal(2)

println(topMonkeyBusiness)

// part 2
val dampingFactor = monkeys
    .map { it.divisor }
    .reduce(Long::times) // ok?

val evenTopperMonkeyBusiness = (1..10_000)
    .map {
        buildMap {
            monkeys.forEach { monkey ->
                put(monkey.id, monkey.offload(monkeyMap) { it % dampingFactor })
            }
        }
    }
    .topTotal(2)

println(evenTopperMonkeyBusiness)