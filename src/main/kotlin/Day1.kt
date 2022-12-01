import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.useLines

fun main() {
    val elfs = readElfs(Paths.get("src", "main", "resources", "day1.txt"))

    val task1 = elfs.maxOf { it.sum() }
    println("Task 1: $task1")

    val task2 = elfs.map { it.sum() }
        .sortedDescending()
        .take(3)
        .sum()
    println("Task 2: $task2")
}

fun readElfs(file: Path): List<List<Int>> {
    file.useLines {
        val list = mutableListOf<List<Int>>()
        var currentList = mutableListOf<Int>()

        for (s in it) {
            if (s.isEmpty()) {
                if (currentList.isNotEmpty()) {
                    list.add(currentList)
                    currentList = mutableListOf()
                }

                continue
            }

            val value = s.toInt()

            currentList.add(value)
        }

        if (currentList.isNotEmpty()) {
            list.add(currentList)
        }

        return list
    }
}
