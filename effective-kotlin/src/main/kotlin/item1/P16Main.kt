package item1

import kotlin.properties.Delegates

fun main(args: Array<String>) {
    val list1: MutableList<Int> = mutableListOf()
    var list2: List<Int> = listOf()

    var names by Delegates.observable(listOf<String>()) { _, oldValue, newValue ->
        println("Names changed from $oldValue to $newValue")
    }

    // list1 컬랙션 내부에 요소를 추가하는 것(상태를 가지게 됨)
    list1 += 1 // list1.add(1)

    // 새로운 ArrayList 객체 생성후 기존 list2를 넣고
    // 새로운 요소(2)를 추가하고 생성된 객체 반환
    list2 += 2 // list2.plus(1)

    names += "Fabio"
    names += "Bill"
}

