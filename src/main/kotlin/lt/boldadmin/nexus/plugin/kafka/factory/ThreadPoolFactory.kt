package lt.boldadmin.nexus.plugin.kafka.factory

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadPoolFactory {
    fun create(subscriberCount: Int): ExecutorService = Executors.newFixedThreadPool(TOPIC_COUNT + subscriberCount)

    companion object {
        private const val TOPIC_COUNT = 3
    }
}
