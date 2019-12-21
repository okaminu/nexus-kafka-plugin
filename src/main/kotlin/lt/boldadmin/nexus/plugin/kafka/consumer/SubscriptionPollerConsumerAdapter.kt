package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.SubscriptionPoller
import java.util.concurrent.ExecutorService

class SubscriptionPollerConsumerAdapter(
    private val coordinatesConsumer: CollaboratorCoordinatesConsumer,
    private val messageConsumer: CollaboratorMessageConsumer,
    private val executor: ExecutorService
): SubscriptionPoller {

    override fun poll() {
        executor.apply {
            submit { coordinatesConsumer.consumeAbsent() }
            submit { coordinatesConsumer.consumeCoordinates() }
            submit { messageConsumer.consumeMessages() }
        }
    }
}
