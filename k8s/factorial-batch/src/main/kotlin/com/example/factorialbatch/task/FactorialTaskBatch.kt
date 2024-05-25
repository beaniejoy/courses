package com.example.factorialbatch.task

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.transaction.PlatformTransactionManager
import java.math.BigDecimal
import java.util.stream.IntStream

@Configuration
class FactorialTaskBatch {
    @Bean
    fun taskReader(redisTemplate: StringRedisTemplate): ItemReader<String> {
        return ItemReader { redisTemplate.opsForSet().pop("factorial:task-queue") }
    }

    @Bean
    fun taskProcessor(): ItemProcessor<String, String> {
        return ItemProcessor { task ->
            val n = task.toInt()
            var result = BigDecimal.ONE

            IntStream.range(2, n + 1).forEach {
                result = result.multiply(BigDecimal(it))
            }

            "${task}:${result.toPlainString()}"
        }
    }

    @Bean
    fun resultWriter(redisTemplate: StringRedisTemplate): ItemWriter<String> {
        return ItemWriter { items ->
            val resultMap = items.associate {
                val factorial = it.split(":")
                factorial[0] to factorial[1]
            }

            redisTemplate.opsForHash<String, String>().putAll("factorial:result-set", resultMap)
        }
    }

    @Bean
    fun factorialStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        taskReader: ItemReader<String>,
        taskProcessor: ItemProcessor<String, String>,
        resultWriter: ItemWriter<String>
    ): Step {
        return StepBuilder("factorial-step", jobRepository)
            .chunk<String, String>(10, transactionManager)
            .reader(taskReader)
            .processor(taskProcessor)
            .writer(resultWriter)
            .build()
    }

    @Bean
    fun factorialTaskJob(
        factorialStep: Step,
        jobRepository: JobRepository
    ): Job {
        return JobBuilder("factorial-job", jobRepository)
            .start(factorialStep)
            .build()
    }
}