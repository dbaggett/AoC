import java.io.File

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day6.txt".asFile().readText()

fun marker(length: Int) = input.foldIndexed("") { index, marker, letter ->
    if (marker.length > length) {
        marker
    } else {
        val temp = if (marker.contains(letter)) {
            val position = marker.indexOf(letter) + 1
            marker.drop(position) + letter
        } else {
            marker + letter
        }

        if (temp.length == length) {
            temp + "-${index + 1}"
        } else {
            temp
        }
    }
}

// part 1
println(marker(4).split("-")[1])

// part 2
println(marker(14).split("-")[1])