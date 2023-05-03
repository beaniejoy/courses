package item4

interface CarFactory {
    companion object {
        // type inference에 의해 자동으로 타입 지정
        // Car 말고 Fiat126P 클래스 타입으로 자동 지정
        val DEFAULT_CAR = Fiat126P()
    }

    fun produce() = DEFAULT_CAR
}

class CarFactoryImpl: CarFactory {
    override fun produce(): Fiat126P {
        return Fiat126P()
    }
}

open class Car
class Fiat126P: Car()

fun main() {

}