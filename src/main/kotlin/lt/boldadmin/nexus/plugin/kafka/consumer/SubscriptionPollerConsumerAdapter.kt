package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.SubscriptionPoller
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class SubscriptionPollerConsumerAdapter(
    private val coordinatesConsumer: CollaboratorCoordinatesConsumer,
    private val messageConsumer: CollaboratorMessageConsumer
): SubscriptionPoller {

    final override fun poll() {
        create().apply {
            submit { coordinatesConsumer.consumeAbsent() }
            submit { coordinatesConsumer.consumeCoordinates() }
            submit { messageConsumer.consumeMessages() }
        }
    }

    protected open fun create(): ExecutorService = Executors.newFixedThreadPool(3)
}
