package item1

// data class > equals & hascode 구현
data class FullName(
    var name: String,
    var surname: String
)

fun main(args: Array<String>) {
    val names = mutableSetOf<FullName>()
    val person = FullName("AAA", "AAA")

    names.add(person)
    names.add(FullName("Jordan", "Hansen"))
    names.add(FullName("David", "Blanc"))

    println(names)
    println(person in names)

    person.name = "ZZZ"
    println(names)
    println(person in names)
}