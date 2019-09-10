package lt.boldadmin.nexus.plugin.eventkafka.consumer

import lt.boldadmin.nexus.api.event.subscriber.CollaboratorCoordinatesSubscriber
import lt.boldadmin.nexus.api.event.subscriber.CollaboratorMessageSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.eventkafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.eventkafka.deserializer.CollaboratorMessageDeserializer
import lt.boldadmin.nexus.plugin.eventkafka.factory.ConsumerPropertiesFactory

class CollaboratorLocationConsumer(
    private val consumerPropertiesFactory: ConsumerPropertiesFactory,
    private val consumer: Consumer,
    private val coordinatesSubscribers: Collection<CollaboratorCoordinatesSubscriber> = listOf(),
    private val messageSubscribers: Collection<CollaboratorMessageSubscriber> = listOf()
) {
    fun consumeCoordinates() = consumer.consume<Pair<String, Coordinates>>(
        "collaborator-location-update-by-coordinates",
        { coordinatesSubscribers.forEach { subscriber -> subscriber.notify(it.first, it.second) }},
        consumerPropertiesFactory.create(CollaboratorCoordinatesDeserializer::class.java)
    )

    fun consumeMessages() = consumer.consume<Message>(
        "collaborator-location-update-by-message",
        { messageSubscribers.forEach { subscriber -> subscriber.notify(it) }},
        consumerPropertiesFactory.create(CollaboratorMessageDeserializer::class.java)
    )
}