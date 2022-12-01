import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

fun main() {
    val days = discoverDays()

    println("Advent of Code 2022!")
    println("You can run any of the following days:")

    days.keys.forEach {
        println("- $it")
    }
    println("Which do you want to run?")
    print("> ")

    val name = readln()
    val day = days[name] ?: run {
        println("No day with that name found!")
        return
    }

    println("Running $name...")
    day()
}

fun discoverDays(): Map<String, () -> Unit> {
    val days = mutableMapOf<String, () -> Unit>()

    for (i in 1.. 25) {
        val method = classOrNull("Day${i}Kt")
            ?.getMethodOrNull("main") ?: continue
        days["Day$i"] = { method.tryInvokeStatic() }
    }

    return days
}

fun Method.tryInvokeStatic(vararg parameters: Any?): Any? {
    return try {
        invoke(null, *parameters)
    } catch (_: InvocationTargetException) {
        null
    } catch (_: ExceptionInInitializerError) {
        null
    }
}

fun Class<*>.getMethodOrNull(name: String, vararg parameters: Class<*>): Method? {
    return try {
        getMethod(name, *parameters)
    } catch (_: NoSuchMethodError) {
        null
    }
}

fun classOrNull(name: String): Class<*>? {
    return try {
        Class.forName(name)
    } catch (_: ClassNotFoundException) {
        null
    }
}
