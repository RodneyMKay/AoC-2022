import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.useLines

fun main() {
    val file = Paths.get("src", "main", "resources", "day2.txt")

    val task1 = readStrategyTask1(file).sumOf { it.second.getScoreAgainst(it.first) }
    println("Task 1: $task1")

    val task2 = readStrategyTask2(file).sumOf { it.second.getScoreAgainst(it.first) }
    println("Task 2: $task2")
}

private val shapeInputNames = mapOf(
    "A" to Shape.ROCK,
    "B" to Shape.PAPER,
    "C" to Shape.SCISSORS,
    "X" to Shape.ROCK,
    "Y" to Shape.PAPER,
    "Z" to Shape.SCISSORS
)

fun readStrategyTask1(file: Path) = sequence {
    file.useLines { lines ->
        lines.map { it.split(" ") }
            .filter { it.size == 2 }
            .map { it[0] to it[1] }
            .mapNotNull { (first, second) -> shapeInputNames[first]?.let { it to second } }
            .mapNotNull { (first, second) -> shapeInputNames[second]?.let { first to it } }
            .forEach { yield(it)  }
    }
}

fun readStrategyTask2(file: Path) = sequence {
    file.useLines { lines ->
        lines.map { it.split(" ") }
            .filter { it.size == 2 }
            .map { it[0] to it[1] }
            .mapNotNull { (first, second) -> shapeInputNames[first]?.let { it to second } }
            .map { (first, second) -> first to getStrategicShape(first, second) }
            .forEach { yield(it)  }
    }
}

private val winningFigures by lazy {
    Shape.values().associateWith { figure ->
        (Shape.winsAgainst.find { it.second == figure }?.first ?: error("There's no way to win against $figure"))
    }
}

private val loosingFigures by lazy {
    Shape.values().associateWith { figure ->
        (Shape.winsAgainst.find { it.first == figure }?.second ?: error("There's no way to win against $figure"))
    }
}

fun getStrategicShape(opponentShape: Shape, outcome: String): Shape {
    return when (outcome) {
        "X" -> loosingFigures[opponentShape] ?: error("Cannot find loosing figure!")
        "Y" -> opponentShape
        "Z" -> winningFigures[opponentShape] ?: error("Cannot find winning figure!")
        else -> error("Invalid outcome value: $outcome")
    }
}

enum class Shape(
    private val score: Int
) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    fun getScoreAgainst(other: Shape): Int {
        return this.score + when {
            winsAgainst.contains(this to other) -> 6
            winsAgainst.contains(other to this) -> 0
            else -> 3
        }
    }

    companion object {
        val winsAgainst = setOf(
            ROCK to SCISSORS,
            PAPER to ROCK,
            SCISSORS to PAPER
        )
    }
}
