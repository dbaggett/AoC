package baggett.aoc

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import kotlin.coroutines.*

/**This list represents the Calories of the food carried by five Elves:

The first Elf is carrying food with 1000, 2000, and 3000 Calories, a total of 6000 Calories.
The second Elf is carrying one food item with 4000 Calories.
The third Elf is carrying food with 5000 and 6000 Calories, a total of 11000 Calories.
The fourth Elf is carrying food with 7000, 8000, and 9000 Calories, a total of 24000 Calories.
The fifth Elf is carrying one food item with 10000 Calories.
In case the Elves get hungry and need extra snacks, they need to know which Elf to ask: they'd like to know how many Calories are being carried by the Elf carrying the most Calories. In the example above, this is 24000 (carried by the fourth Elf).

Find the Elf carrying the most Calories. How many total Calories is that Elf carrying?**/

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day1.txt".asFile().readLines()

val emptyIndices = input.indices
    .filter { input[it] == ""}
    .toList()

val calories = (emptyIndices.zipWithNext() +
    listOf(Pair(0, emptyIndices[0]), Pair(emptyIndices.last(), input.size)))
    .map { pair ->
        input.subList(pair.first, pair.second)
            .filter { it != "" }
            .sumOf { it.toInt() }
    }.sorted()

println(calories.maxOrNull() ?: 0)

println(calories.takeLast(3).sum())