package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.subscriber.CollaboratorCoordinatesSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.kafka.factory.ConsumerPropertiesFactory
import org.apache.kafka.common.serialization.StringDeserializer

class CollaboratorLocationConsumer(
    private val consumerPropertiesFactory: ConsumerPropertiesFactory,
    private val consumer: Consumer,
    private val coordinatesSubscribers: Collection<CollaboratorCoordinatesSubscriber> = listOf()
) {
    fun consumeCoordinates() = consumer.consume<Pair<String, Coordinates>>(
        "collaborator-coordinates-update",
        { coordinatesSubscribers.forEach { subscriber -> subscriber.notify(it.first, it.second) } },
        consumerPropertiesFactory.create(CollaboratorCoordinatesDeserializer::class.java)
    )

    fun consumerAbsent() = consumer.consume<String>(
        "collaborator-coordinates-absent",
        { coordinatesSubscribers.forEach { subscriber -> subscriber.notifyAbsent(it) } },
        consumerPropertiesFactory.create(StringDeserializer::class.java)
    )
}
