package io.beaniejoy.coresecurity.util

import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class Transaction(val transactionManager: PlatformTransactionManager) {

    companion object : KLogging()

    operator fun <T> invoke(
        readOnly: Boolean = true,
        propagation: Propagation = Propagation.PROPAGATION_REQUIRED,
        rollbackCondition: (() -> Boolean)? = null,
        body: () -> T
    ): T {
        val def = DefaultTransactionDefinition()
        def.propagationBehavior = propagation.value
        def.isReadOnly = readOnly
        val transactionStatus = transactionManager.getTransaction(def)
        val result: T
        try {
            result = body()
        } catch (e: Exception) {
            transactionManager.rollback(transactionStatus)
            throw e
        }
        if (rollbackCondition?.invoke() == true) {
            transactionManager.rollback(transactionStatus)
        } else {
            transactionManager.commit(transactionStatus)
        }
        return result
    }
}

enum class Propagation(val value: Int) {
    PROPAGATION_REQUIRED(0),
    PROPAGATION_SUPPORTS(1),
    PROPAGATION_MANDATORY(2),
    PROPAGATION_REQUIRES_NEW(3),
    PROPAGATION_NOT_SUPPORTED(4),
    PROPAGATION_NEVER(5),
    PROPAGATION_NESTED(6);
}