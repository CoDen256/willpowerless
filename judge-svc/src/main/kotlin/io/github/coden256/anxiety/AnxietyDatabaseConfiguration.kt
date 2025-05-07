package io.github.coden256.anxiety

import io.github.coden256.database.DatasourceConfig
import io.github.coden256.database.database
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "db")
data class AnxietyDatabaseProperties(
    val anxiety: DatasourceConfig
)

@Configuration
@EnableConfigurationProperties(AnxietyDatabaseProperties::class)
class AnxietyDatabaseConfiguration {

    @Bean
    fun anxietyDatabase(properties: AnxietyDatabaseProperties): Database{
        return database(properties.anxiety)
    }

}