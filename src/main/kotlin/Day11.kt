import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

fun main() {
    val monkeys = readMonkeys(Paths.get("src", "main", "resources", "day11.txt"))

    val task1Monkeys = monkeys.map { it.copy(items = it.items.toMutableList()) } // Ugly way to deepcopy
    task1Monkeys.playRounds(20, 3)
    val task1 = task1Monkeys.sortedByDescending { it.inspectionCount }
        .take(2)
        .map { it.inspectionCount.toLong() }
        .reduce { acc, monkey -> acc * monkey }

    println("Task 1: $task1")

    val task2Monkeys = monkeys.map { it.copy(items = it.items.toMutableList()) } // Ugly way to deepcopy
    task2Monkeys.playRounds(10000, 1)
    val task2 = task2Monkeys.sortedByDescending { it.inspectionCount }
        .take(2)
        .map { it.inspectionCount.toLong() }
        .reduce { acc, monkey -> acc * monkey }

    println("Task 2: $task2")
}

fun List<Monkey>.playRounds(number: Int, roundDivisor: Int) {
    val overallDivisor = this.map { it.divisor }
        .reduce { acc, i -> acc * i }

    repeat(number) {
        for (monkey in this) {
            while (monkey.items.isNotEmpty()) {
                monkey.inspectionCount++
                val item = monkey.items.removeFirst()
                val newItem = monkey.operation(item) / roundDivisor % overallDivisor

                val targetMonkey = when {
                    newItem % monkey.divisor == 0L -> monkey.nextMonkeyIfTrue
                    else -> monkey.nextMonkeyIfFalse
                }

                this[targetMonkey].items.add(newItem)
            }
        }
    }
}

fun readMonkeys(file: Path): List<Monkey> {
    return file.readText()
        .replace("\r\n", "\n")
        .split("\n\n")
        .map { it.lines() }
        .map { it.toMonkey() }
}

val operationRegex = """ {2}Operation: new = (\d+|old) ?([*+]) ?(\d+|old)""".toRegex()
val testRegex = """ {2}Test: divisible by (\d+)\n {4}If true: throw to monkey (\d+)\n {4}If false: throw to monkey (\d+)""".toRegex()

fun List<String>.toMonkey(): Monkey {
    check(size == 6) { "Monkey input must have exactly 6 lines!" }
    check(get(0).startsWith("Monkey ")) { "First line of Monkey description must provide monkey name!" }

    val startingItems = get(1).removePrefix("  Starting items: ")
        .split(", ")
        .map { it.toLong() }
        .toMutableList()

    val (leftString, operator, rightString) = operationRegex.find(get(2))?.destructured
        ?: error("Invalid monkey operation description: \"${get(2)}\"")

    val leftOperation = leftString.toLongOrNull()?.let { { _: Long -> it } } ?: { it }
    val rightOperation = rightString.toLongOrNull()?.let { { _: Long -> it } } ?: { it }

    val operation: (Long) -> Long = when (operator) {
        "*" -> { { leftOperation(it) * rightOperation(it) } }
        "+" -> { { leftOperation(it) + rightOperation(it) } }
        else -> error("Invalid monkey operation: $operator")
    }

    val (divisor, monkeyIfTrue, monkeyIfFalse) = testRegex.find(subList(3, 6).joinToString("\n"))
        ?.destructured ?: error("Invalid test for monkey!")

    return Monkey(
        startingItems,
        operation,
        divisor.toLong(),
        monkeyIfTrue.toInt(),
        monkeyIfFalse.toInt()
    )
}

data class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val divisor: Long,
    val nextMonkeyIfTrue: Int,
    val nextMonkeyIfFalse: Int
) {
    var inspectionCount = 0
}
