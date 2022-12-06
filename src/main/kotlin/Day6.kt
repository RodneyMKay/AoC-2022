import java.nio.file.Paths
import kotlin.io.path.readText

fun main() {
    val input = Paths.get("src", "main", "resources", "day6.txt").readText()

    val task1 = findMarkerWord(4, input)
    println("Task 1: $task1")

    val task2 = findMarkerWord(14, input)
    println("Task 2: $task2")
}

fun findMarkerWord(size: Int, input: String): Int? {
    for (i in size..input.lastIndex) {
        if (input.subSequence((i - size), i).toSet().size == size) {
            return i
        }
    }

    return null
}
