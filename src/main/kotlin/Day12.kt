import java.nio.file.Path
import java.nio.file.Paths
import java.util.PriorityQueue
import kotlin.io.path.readLines

fun main() {
    val input = readPuzzle(Paths.get("src", "main", "resources", "day12.txt"))

    val task1 = input.copy().solve(input.start)!!.count()
    println("Task 1: $task1")

    val task2 = input.field.indexOfChars('a')
        .map { input.copy().solve(it)?.count() ?: Integer.MAX_VALUE }
        .min()
    println("Task 2: $task2")
}

fun Puzzle.solve(startPosition: Pair<Int, Int>): List<Pair<Int, Int>>? {
    val reachableFields = PriorityQueue<Field> { o1, o2 -> o1.distance - o2.distance }

    reachableFields.offer(Field(startPosition.first, startPosition.second, 0, null))

    while (reachableFields.isNotEmpty()) {
        val reachableField = reachableFields.poll()

        if (reachableField.x to reachableField.y == end) {
            return reconstructPath(reachableField)
        }

        val currentField = field[reachableField.x][reachableField.y]
        field[reachableField.x][reachableField.y] = ' '

        val neighborPositions = listOf(
            reachableField.x + 1 to reachableField.y,
            reachableField.x to reachableField.y + 1,
            reachableField.x - 1 to reachableField.y,
            reachableField.x to reachableField.y - 1
        )

        for (neighborPosition in neighborPositions) {
            if (neighborPosition.first !in field.indices
                || neighborPosition.second !in field[neighborPosition.first].indices) {
                continue
            }

            val neighbor = field[neighborPosition.first][neighborPosition.second]

            if (neighbor != ' ' && currentField canTravelTo neighbor) {
                reachableFields.offer(
                    Field(
                        neighborPosition.first,
                        neighborPosition.second,
                        reachableField.distance + 1,
                        reachableField
                    )
                )
            }
        }
    }

    return null
}

fun reconstructPath(reachableField: Field): List<Pair<Int, Int>> {
    val path = mutableListOf<Pair<Int, Int>>()
    var currentField = reachableField.previous

    while (currentField != null) {
        path += currentField.x to currentField.y
        currentField = currentField.previous
    }

    return path.asReversed()
}

infix fun Char.canTravelTo(other: Char): Boolean {
    return this == 'S' && other == 'a'
            || this.code + 1 >= other.code && other != 'E'
            || this == 'z' && other == 'E'
}

data class Field(
    val x: Int,
    val y: Int,
    val distance: Int,
    val previous: Field?
)

fun readPuzzle(file: Path): Puzzle {
    val field = file.readLines()
        .map { it.toCharArray() }
        .toTypedArray()

    val start = field.indexOfChars('S').singleOrNull() ?: error("Puzzle start not found!")
    val end = field.indexOfChars('E').singleOrNull() ?: error("Puzzle end not found!")

    return Puzzle(field, start, end)
}

fun Array<CharArray>.indexOfChars(searchChar: Char) = sequence {
    forEachIndexed { i, chars ->
        chars.forEachIndexed { j, char ->
            if (char == searchChar) {
                yield(i to j)
            }
        }
    }
}

class Puzzle(
    val field: Array<CharArray>,
    val start: Pair<Int, Int>,
    val end: Pair<Int, Int>
) {
    fun copy(): Puzzle {
        return Puzzle(
            field.map { it.copyOf() }.toTypedArray(),
            start,
            end
        )
    }
}
