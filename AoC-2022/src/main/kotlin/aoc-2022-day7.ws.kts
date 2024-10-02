import java.io.File

fun String.asFile(): File = File(this)

val input = "/Users/danny.baggett/projects/AoC-2022/src/main/resources/aoc-2022-input/day7.txt".asFile().readLines()

sealed interface Listing

sealed interface NodeSize {
    fun size(): Int
}

sealed class Operation() : Listing {
    object List : Operation()

    data class ChangeDirectory(val directory: String): Operation() {
        fun isReturn() = directory == ".."
    }

    companion object {
        fun fromString(command: String): Operation {
            val parts = command.split(" ")

            return when (parts[0]) {
                "cd" -> ChangeDirectory(parts[1])
                else -> List
            }
        }
    }
}

sealed class Node() : NodeSize, Listing {
    data class NodeDirectory(val name: String, var children: MutableList<Node>, val parent: NodeDirectory?): Node() {
        override fun size() = children.fold(0) { acc, node ->
            acc + node.size()
        }
    }

    data class NodeFile(val name: String, val size: Int, val parent: NodeDirectory): Node() {
        override fun size() = size
    }

    companion object {
        fun fromString(listing: String, currentDirectory: NodeDirectory): Node {
            val parts = listing.split(" ")

            return when (parts[0]) {
                "dir" -> NodeDirectory(parts[1], mutableListOf(), currentDirectory)
                else -> NodeFile(parts[1], parts[0].toInt(), currentDirectory)
            }
        }
    }
}

fun String.toListing(currentDirectory: Node.NodeDirectory): Listing {
    val parts = this.split(" ")

    return when (parts[0]) {
        "$" -> Operation.fromString(parts.slice(1 until parts.size).joinToString(" "))
        else -> Node.fromString(this, currentDirectory)
    }
}

val rootFileSystem = input.drop(1)
val directories = mutableListOf<Node.NodeDirectory>()
var root = Node.NodeDirectory("/", mutableListOf(), null)
var current = root

rootFileSystem.forEach { line ->
    when (val listing = line.toListing(current)) {
        is Operation -> when (listing) {
            is Operation.List -> return@forEach
            is Operation.ChangeDirectory -> {
                if (listing.directory == "..") {
                    current.parent?.let {
                        current = it
                    }
                } else {
                    current.children
                        .filterIsInstance<Node.NodeDirectory>()
                        .find { it.name == listing.directory }
                        ?.let {
                            current = it
                        }
                }
            }
        }
        is Node -> when (listing) {
            is Node.NodeDirectory -> {
                val directory = current.children
                    .filterIsInstance<Node.NodeDirectory>()
                    .find { it.name == listing.name }

                if (directory == null) {
                    directories.add(listing)
                    current.children.add(listing)
                }
            }
            is Node.NodeFile -> {
                val file = current.children
                    .filterIsInstance<Node.NodeFile>()
                    .find { it.name == listing.name }

                if (file == null) {
                    current.children.add(listing)
                }
            }
        }
    }
}

// part 1
println(directories
    .filter { it.size() < 100000 }
    .fold(0) { acc, node ->
        acc + node.size()
    }
)

// part 2
println((root.size() - 40000000).let { difference ->
    directories.map { it.size() }.sorted().first { it > difference }
})