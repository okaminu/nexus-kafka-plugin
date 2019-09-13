package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.SubscriptionPoller

class SubscriptionPollerConsumerAdapter(
    private val locationConsumer: CollaboratorLocationConsumer
): SubscriptionPoller {

    override fun pollInNewThread() {
        Thread { run { locationConsumer.consumeCoordinates() } }.start()
        Thread { run { locationConsumer.consumeMessages() } }.start()
    }
}