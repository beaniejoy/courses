package com.example.notificationbatch

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.transaction.PlatformTransactionManager
import java.time.ZonedDateTime
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
class NotificationBatch {
    @Bean
    fun reader(dataSource: DataSource): JdbcCursorItemReader<NotificationInfo> {
        return JdbcCursorItemReaderBuilder<NotificationInfo>()
            .name("followerReader")
            .dataSource(dataSource)
            .sql(
                """
                 |SELECT
                 |  f.follow_id, 
                 |  u.email, u.username, 
                 |  f.follower_id, 
                 |  u2.username AS follower_name, 
                 |  f.follow_datetime
                 |FROM follow f, user u, user u2
                 |WHERE f.user_id = u.user_id 
                 |AND f.follower_id = u2.user_id 
                 |AND f.mail_sent_datetime is null;
             """.trimMargin()
            )
            .rowMapper(BeanPropertyRowMapper(NotificationInfo::class.java))
            .build()
    }

    @Bean
    fun sendMail(
        mailSender: JavaMailSender,
        jdbcTemplate: JdbcTemplate
    ): ItemWriter<NotificationInfo> {
        return ItemWriter { items ->
            items.forEach { item ->
                val message = SimpleMailMessage().apply {
                    from = "temp@test.com"
                    setTo(item.email)
                    subject = "new follower!"
                    text = "Hello ${item.username}! @${item.followerName} is now follow you!"
                }

                mailSender.send(message)

                jdbcTemplate.update(
                    "UPDATE follow SET mail_sent_datetime = ? WHERE follow_id = ?",
                    ZonedDateTime.now(),
                    item.followId
                )
            }
        }
    }

    @Bean
    fun notificationStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        itemReader: ItemReader<NotificationInfo>,
        itemWriter: ItemWriter<NotificationInfo>
    ): Step {
        return StepBuilder("mail-send-step", jobRepository)
            .chunk<NotificationInfo, NotificationInfo>(10, transactionManager)
            .reader(itemReader)
            .writer(itemWriter)
            .build()
    }

    @Bean
    fun notificationJob(notificationStep: Step, jobRepository: JobRepository): Job {
        return JobBuilder("mail-send-job", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(notificationStep)
            .build()
    }
}