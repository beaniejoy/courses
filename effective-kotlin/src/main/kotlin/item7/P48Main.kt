package item7

import module.mapper
import java.io.IOException

/**
 * 예측 가능한, 예상되는 오류를 표현할 때 Failure, null을 사용하자 (throw Exception 대신에)
 */

class JsonParsingException: RuntimeException()
data class Dress(val color: String?)
data class Person(
    val outfit: Any? = null,
    val email: String? = null,
)

// null 활용
inline fun <reified T> String.readObjectOrNull(): T? {
    return try {
        mapper.readValue(this, T::class.java)
    } catch (e: IOException) {
        null
    } catch (e: Exception) {
        // 예상치 못한 예외 처리시 throw
        throw e
    }
}

// Failure 활용
inline fun <reified T> String.readObject(): Result<T> {
    return try {
        val result = mapper.readValue(this, T::class.java)
        Success(result)
    } catch (e: IOException) {
        Failure(JsonParsingException())
    } catch (e: Exception) {
        // 예상치 못한 예외 처리시 throw
        throw e
    }
}

sealed class Result<out T>
class Success<out T>(val result: T) : Result<T>()
class Failure(val throwable: Throwable) : Result<Nothing>()

fun main() {
    val userTextSuccess = "{\"outfit\": {\"color\": \"blue\"}, \"email\": \"test@email.com\"}"
    val userTextFailure = "{\"aaa\": {\"color\": \"blue\"}, \"bbb\": \"test@email.com\"}"
    val person = userTextFailure.readObject<Person>()
    val email = when (person) {
        is Success -> person.result
        is Failure -> null
    }

    println("result: $email")
}