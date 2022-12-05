import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

fun main() {
    val file = Paths.get("src", "main", "resources", "day5.txt")

    task1(file)
    task2(file)
}

fun task1(file: Path) {
    val (ship, moves) = readContainerPuzzle(file)

    for (move in moves) {
        repeat(move.amount) {
            ship[move.to].add(ship[move.from].removeLast())
        }
    }

    val task1 = ship.map { if (it.isNotEmpty()) it.last() else "" }
        .joinToString("")

    println("Task 1: $task1")
}

fun task2(file: Path) {
    val (ship, moves) = readContainerPuzzle(file)

    for (move in moves) {
        ship[move.to].addAll(ship[move.from].takeLast(move.amount))
        repeat(move.amount) { ship[move.from].removeLast() }
    }

    val task2 = ship.map { if (it.isNotEmpty()) it.last() else "" }
        .joinToString("")

    println("Task 2: $task2")
}

private val moveRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

fun readContainerPuzzle(file: Path): Pair<Ship, List<Move>> {
    val text = file.readText().replace("\r\n", "\n")

    val containers = text.substringBefore("\n\n")
        .split("\n")
        .dropLast(1)
        .map { it.chunked(4) }
        .asReversed()
        .map { list -> list.map { it[1] } }

    val stacks = List(containers.first().size) { mutableListOf<Char>() }

    for (containerRow in containers) {
        for ((i, container) in containerRow.withIndex()) {
            if (container != ' ') {
                stacks[i].add(container)
            }
        }
    }

    val moves = text.substringAfter("\n\n")
        .split("\n")
        .mapNotNull { moveRegex.find(it)?.destructured }
        .map { (amount, from, to) -> Move(from.toInt() - 1, to.toInt() - 1, amount.toInt()) }

    return stacks to moves
}

typealias Ship = List<MutableList<Char>>

data class Move(
    val from: Int,
    val to: Int,
    val amount: Int
)
