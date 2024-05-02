package com.example.factorialcache.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class IncorrectApiKeyException(msg: String) : RuntimeException(msg)
