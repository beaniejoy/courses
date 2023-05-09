package item8

fun main() {
    println("What is your name?")
    val name = readlnOrNull()
    // name.isNullOrBlank().not()은 type casting이 안된다. (Boolean에 지정된 operator function)
    if (!name.isNullOrBlank()) {
        println("Hello ${name.uppercase()}")
    }
}