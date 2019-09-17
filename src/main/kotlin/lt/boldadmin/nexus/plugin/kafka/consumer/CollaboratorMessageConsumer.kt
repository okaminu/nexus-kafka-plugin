package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.subscriber.CollaboratorMessageSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorMessageDeserializer
import lt.boldadmin.nexus.plugin.kafka.factory.ConsumerPropertiesFactory

class CollaboratorMessageConsumer(
    private val consumerPropertiesFactory: ConsumerPropertiesFactory,
    private val consumer: Consumer,
    private val messageSubscribers: Collection<CollaboratorMessageSubscriber> = listOf()
) {
    fun consumeMessages() = consumer.consume<Message>(
        "collaborator-message",
        { messageSubscribers.forEach { subscriber -> subscriber.notify(it) } },
        consumerPropertiesFactory.create(CollaboratorMessageDeserializer::class.java)
    )
}
