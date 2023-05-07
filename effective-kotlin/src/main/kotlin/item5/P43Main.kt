package item5

/**
 * require 내용에 따라 smart cast 가능
 */
class Dress
class Person(
    val outfit: Any,
    val email: String?
)

fun changeDress(person: Person) {
    require(person.outfit is Dress)
    val dress: Dress = person.outfit // smart cast (원래는 compile error)
}

fun sendEmail(person: Person, message: String) {
    require(person.email != null)
    val email: String = person.email // smart cast
}

fun main() {

}