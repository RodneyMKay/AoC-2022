import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readLines
import kotlin.math.abs

fun main() {
    val moves = readMoves(Paths.get("src", "main", "resources", "day9.txt"))

    val task1 = getTailPositions(2, moves).distinct().count()
    println("Task 1: $task1")

    val task2 = getTailPositions(10, moves).distinct().count()
    println("Task 2: $task2")
}

fun getTailPositions(knotCount: Int, moves: List<Pair<Pair<Int, Int>, Int>>) = sequence {
    val knots = Array(knotCount) { 0 to 0 }
    yield(knots.last())

    for ((direction, distance) in moves) {
        val (dx, dy) = direction

        repeat(distance) {
            knots[0] = knots[0].first + dx to knots[0].second + dy

            for (i in 1..knots.lastIndex) {
                val currentKnot = knots[i]
                val previousKnot = knots[i - 1]

                if (abs(currentKnot.first - previousKnot.first) > 1 || abs(currentKnot.second - previousKnot.second) > 1) {
                    val tailDx = when {
                        previousKnot.first - currentKnot.first > 0 -> 1
                        previousKnot.first - currentKnot.first < 0 -> -1
                        else -> 0
                    }

                    val tailDy = when {
                        previousKnot.second - currentKnot.second > 0 -> 1
                        previousKnot.second - currentKnot.second < 0 -> -1
                        else -> 0
                    }

                    knots[i] = currentKnot.first + tailDx to currentKnot.second + tailDy
                }
            }

            yield(knots.last())
        }
    }
}

fun readMoves(file: Path): List<Pair<Pair<Int, Int>, Int>> {
    fun String.toDirection() = when (this) {
        "R" -> 1 to 0
        "D" -> 0 to 1
        "L" -> -1 to 0
        "U" -> 0 to -1
        else -> error("Invalid direction: $this")
    }

    return file.readLines()
        .map { it.substringBefore(" ").toDirection() to it.substringAfter(" ").toInt() }
}
