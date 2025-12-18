package show.grip.mms

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GripMediaSystemApplication

fun main(args: Array<String>) {
    // Load .env file for local development (if exists)
    try {
        val dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load()

        // Set as system properties so Spring can resolve ${...} placeholders
        dotenv.entries().forEach { entry ->
            System.setProperty(entry.key, entry.value)
        }

        println("✅ .env file loaded successfully (${dotenv.entries().size} variables)")
    } catch (e: Exception) {
        println("⚠️  .env file not found. Using system environment variables.")
    }

    runApplication<GripMediaSystemApplication>(*args)
}