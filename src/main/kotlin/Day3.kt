import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.useLines

fun main() {
    val file = Paths.get("src", "main", "resources", "day3.txt")

    val task1 = readRucksacks(file)
        .map { (first, second) -> first.toSet() to second.toSet() }
        .map { (first, second) -> first.toMutableList().filter { second.contains(it) } }
        .map { it.sum() }
        .sum()
    println("Task 1: $task1")

    val task2 = readRucksacks(file)
        .chunked(3)
        .map { list ->
            list.map { (first, second) -> first.toMutableSet().apply { addAll(second) } }
                .reduce { first, second -> first.apply { first.removeIf { !second.contains(it) } } }
                .sum()
        }
        .sum()
    println("Task 2: $task2")
}

fun readRucksacks(file: Path) = sequence {
    file.useLines { lines ->
        for (line in lines) {
            val middle = line.length / 2

            val first = line.substring(middle)
                .map { it.toRucksackPriority() }

            val second = line.substring(0, middle)
                .map { it.toRucksackPriority() }

            check(first.size == second.size) {
                "Unequal size: $first / $second"
            }

            yield(first to second)
        }
    }
}

fun Char.toRucksackPriority(): Int {
    return when (this) {
        in 'a'..'z' -> this.code - 'a'.code + 1
        in 'A'..'Z' -> this.code - 'A'.code + 27
        else -> throw IllegalArgumentException("Not a valid rucksack character: $this")
    }
}
