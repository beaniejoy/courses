package item2

/**
 * kotlin sequence > lazy evaluation
 */
fun main() {
    val primes: Sequence<Int> = sequence {
        var numbers = generateSequence(2) { it + 1 }

        var prime: Int
        while (true) {
            println("### start")
            prime = numbers.first() // 이 때 sequence lazy되었던 내용이 계산됨

            println("### prime $prime")
            yield(prime)

            numbers = numbers
                .drop(1)
                .filter {
                    println("filter $it % $prime")
                    it % prime != 0
                }
        }
    }

    println(primes.take(10).toList())
}