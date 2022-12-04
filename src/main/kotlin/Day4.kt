import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.useLines
import kotlin.math.max
import kotlin.math.min

fun main() {
    val idPairs = readIdPairs(Paths.get("src", "main", "resources", "day4.txt"))

    val task1 = idPairs.count { (first, second) -> first contains second || second contains first }
    println("Task 1: $task1")

    val task2 = idPairs.count { (first, second) -> first intersects second }
    println("Task 2: $task2")
}

fun readIdPairs(file: Path) = sequence {
    file.useLines { lines ->
        for (line in lines) {
            val ranges = line.split(",")
            check(ranges.size == 2) { "Invalid input: $line" }
            yield(readRange(ranges[0]) to readRange(ranges[1]))
        }
    }
}

fun readRange(input: String): IntRange {
    val values = input.split("-")
    val min = values.getOrNull(0)?.toIntOrNull() ?: throw IllegalArgumentException("Invalid range: $input")
    val max = values.getOrNull(1)?.toIntOrNull() ?: throw IllegalArgumentException("Invalid range: $input")
    return min..max
}

infix fun IntRange.contains(other: IntRange) = this.first <= other.first && this.last >= other.last

infix fun IntRange.intersects(other: IntRange) = (min(last, other.last) - max(first, other.first)) >= 0
