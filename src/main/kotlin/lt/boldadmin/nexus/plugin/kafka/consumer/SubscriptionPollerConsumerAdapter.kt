package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.SubscriptionPoller

open class SubscriptionPollerConsumerAdapter(
    private val locationConsumer: CollaboratorLocationConsumer,
    private val messageConsumer: CollaboratorMessageConsumer
): SubscriptionPoller {

    final override fun pollInNewThread() {
        runInThread { locationConsumer.consumeCoordinates() }
        runInThread { locationConsumer.consumerAbsent() }
        runInThread { messageConsumer.consumeMessages() }
    }

    open fun runInThread(function: () -> Unit) {
        Thread { run { function() } }.start()
    }
}
