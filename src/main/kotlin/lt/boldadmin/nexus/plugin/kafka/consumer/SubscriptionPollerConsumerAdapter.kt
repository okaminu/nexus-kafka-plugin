package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.SubscriptionPoller

class SubscriptionPollerConsumerAdapter(
    private val locationConsumer: CollaboratorLocationConsumer,
    private val messageConsumer: CollaboratorMessageConsumer
): SubscriptionPoller {

    override fun pollInNewThread() {
        Thread { run { locationConsumer.consumeCoordinates() } }.start()
        Thread { run { locationConsumer.consumerAbsent() } }.start()
        Thread { run { messageConsumer.consumeMessages() } }.start()
    }
}
