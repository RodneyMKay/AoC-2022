import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readLines
import kotlin.math.abs

fun main() {
    val forest = readForest(Paths.get("src", "main", "resources", "day8.txt"))

    val task2 = getBestView(forest)

    makeInvisibleTreesNegative(forest)

    val task1 = forest.flatMap { it.asIterable() }
        .count { it >= 0 }
    println("Task 1: $task1")

    println("Task 2: $task2")
}

// Wanted to try implementing this in place, instead keeping track of which positions we've identified as (in-)visible
fun makeInvisibleTreesNegative(forest: Array<IntArray>) {
    // Make everything negative
    for (i in forest.indices) {
        for (j in forest[i].indices) {
            forest[i][j] = -forest[i][j] - 1
        }
    }

    // Force all the visible trees positive
    for (i in forest.indices) {
        val maxes = arrayOf(-1, -1, -1, -1)

        for (j in forest[i].indices) {
            // Check from all cardinal directions at once
            val coordinates = listOf(
                i to j,
                j to forest.lastIndex - i,
                forest.lastIndex - i to forest.lastIndex - j,
                forest.lastIndex - j to i
            )

            for ((index, value) in coordinates.withIndex()) {
                val (x, y) = value

                if (abs(forest[x][y]) > maxes[index]) {
                    maxes[index] = abs(forest[x][y])
                    forest[x][y] = abs(forest[x][y])
                }
            }
        }
    }
}

fun getBestView(forest: Array<IntArray>): Int {
    fun getScore(x: Int, y: Int, dx: Int, dy: Int): Int {
        val height = forest[x][y]
        var view = 0
        var currentX = x
        var currentY = y

        while (true) {
            currentX += dx
            currentY += dy

            if (currentX !in forest.indices || currentY !in forest[x].indices) {
                break
            }

            view++

            if (forest[currentX][currentY] >= height) {
                break
            }
        }

        return view
    }

    var max = 0
    var maxPos = 0 to 0

    for ((x, row) in forest.withIndex()) {
        for ((y, _) in row.withIndex()) {
            val totalScore = getScore(x, y, 1, 0) * getScore(x, y, -1, 0) * getScore(x, y, 0, 1) * getScore(x, y, 0, -1)

            if (totalScore > max) {
                max = totalScore
                maxPos = x to y
            }
        }
    }

    println(maxPos)
    return max
}

fun readForest(file: Path): Array<IntArray> {
    return file.readLines()
        .map { line -> line.map { it.digitToInt() }.toIntArray() }
        .toTypedArray()
}
