import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readLines
import kotlin.math.abs

fun main() {
    val program = readProgram(Paths.get("src", "main", "resources", "day10.txt"))

    val machine = object : VirtualMachine() {
        var totalStrength = 0
        var crtScreen = ""

        override fun handleInterrupt() {
            totalStrength += currentTime * x
        }

        override fun processClockCycle() {
            val crtPosition = currentTime % 40

            if (crtPosition == 0) {
                crtScreen += "\n"
            }

            crtScreen += when {
                abs(x - crtPosition) > 1 -> "."
                else -> "#"
            }
        }
    }

    machine.runProgram(program)

    println("Total Signal Strength: ${machine.totalStrength}")
    print(machine.crtScreen)
}

fun readProgram(file: Path): List<Instruction> {
    fun String.toInstruction(): Instruction {
        return when {
            this == "noop" -> Instruction.Noop
            this.startsWith("addx") -> Instruction.AddX(this.substringAfter(" ").toInt())
            else -> error("Invalid instruction: $this")
        }
    }

    return file.readLines()
        .map { it.toInstruction() }
}

abstract class VirtualMachine {
    var currentTime: Int = 0
    var interruptTime: Int = 20
    var x: Int = 1

    fun runProgram(program: List<Instruction>) {
        for (instruction in program) {
            repeat(instruction.processTime) {
                processClockCycle()
                currentTime++

                if (currentTime == interruptTime) {
                    handleInterrupt()
                    interruptTime += 40
                }
            }

            instruction.execute(this)
        }
    }

    abstract fun handleInterrupt()

    abstract fun processClockCycle()

}

sealed class Instruction {
    abstract val processTime: Int

    abstract fun execute(machine: VirtualMachine)

    object Noop : Instruction() {
        override val processTime = 1

        override fun execute(machine: VirtualMachine) {
        }
    }

    data class AddX(
        val addend: Int
    ) : Instruction() {
        override val processTime = 2

        override fun execute(machine: VirtualMachine) {
            machine.x += addend
        }
    }
}
