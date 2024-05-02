package com.example.factorialcache.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE

@Component
class FileLogger(
    @Value("\${HOSTNAME:UNRESOLVED_HOST}") // k8s에 의해 설정되는 환경 변수
    private val host: String,
) {
    companion object {
        const val LOCAL_LOG_FILE = "/factorial/logs/cache-log.log"
    }

    fun log(message: String) {
        try {
            Files.writeString(
                Paths.get(LOCAL_LOG_FILE),
                "${host}::${message}${System.lineSeparator()}",
                CREATE, APPEND
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}