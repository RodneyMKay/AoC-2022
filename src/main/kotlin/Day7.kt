import java.nio.file.Path
import java.nio.file.Paths
import java.util.Stack
import kotlin.io.path.readLines

fun main() {
    val root = readTerminalOutput(Paths.get("src", "main", "resources", "day7.txt"))
    val sizes = getDirectorySizes(root)

    val task1 = sizes.map { (_, size) -> size }
        .filter { it <= 100_000 }
        .sum()
    println("Task 1: $task1")

    val minSize = 30_000_000L - (70_000_000L - sizes[root]!!)
    val task2 = sizes
        .map { (_, size) -> size }
        .filter { it >= minSize }
        .min()
    println("Task 2: $task2")
}

fun readTerminalOutput(file: Path): Entry.Directory {
    var lines = file.readLines()
    val root = Entry.Directory("/")

    check(lines.first() == "\$ cd /") { "First line must cd to root directory" }
    lines = lines.drop(1)

    val current = Stack<Entry.Directory>()
    current.push(root)

    while (lines.isNotEmpty()) {
        val command = lines.first()
        lines = lines.drop(1)

        when {
            command == "$ ls" -> {
                val entries = lines.takeWhile { !it.startsWith("$") }
                lines = lines.drop(entries.size)

                val (directories, files) = entries.partition { it.startsWith("dir") }

                directories.map { it.substringAfter(" ") }
                    .forEach { current.peek().entries.add(Entry.Directory(it)) }

                files.map { it.substringBefore(" ").toLong() to it.substringAfter(" ") }
                    .forEach { (size, name) -> current.peek().entries.add(Entry.File(name, size)) }
            }

            command.startsWith("$ cd ") -> {
                when (val name = command.substringAfter("$ cd ")) {
                    ".." -> current.pop()
                    else -> {
                        val target = current.peek().entries.find { it.name == name } ?: error("Trying to cd to unknown directory: $name")
                        current.push(target as? Entry.Directory ?: error("Can only cd to directories"))
                    }
                }
            }

            else -> error("Unknown command: $command")
        }
    }

    return root
}

sealed class Entry {
    abstract val name: String

    data class Directory(
        override val name: String,
        val entries: MutableList<Entry> = mutableListOf()
    ) : Entry()

    data class File(
        override val name: String,
        val size: Long
    ) : Entry()
}

fun getDirectorySizes(root: Entry.Directory): Map<Entry.Directory, Long> {
    val map = mutableMapOf<Entry.Directory, Long>()

    fun traverse(entry: Entry): Long {
        return when (entry) {
            is Entry.File -> entry.size
            is Entry.Directory -> {
                entry.entries
                    .sumOf { traverse(it) }
                    .also { map[entry] = it }
            }
        }
    }

    traverse(root)

    return map
}
