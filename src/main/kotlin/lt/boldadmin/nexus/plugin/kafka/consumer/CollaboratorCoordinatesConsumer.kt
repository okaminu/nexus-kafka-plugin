package lt.boldadmin.nexus.plugin.kafka.consumer

import lt.boldadmin.nexus.api.event.subscriber.CollaboratorCoordinatesSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.kafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.kafka.factory.ConsumerPropertiesFactory
import org.apache.kafka.common.serialization.StringDeserializer

class CollaboratorCoordinatesConsumer(
    private val consumerPropertiesFactory: ConsumerPropertiesFactory,
    private val consumer: Consumer,
    private val coordinatesSubscribers: Collection<CollaboratorCoordinatesSubscriber> = listOf()
) {
    fun consumeCoordinates() = consumer.consume(
        "collaborator-coordinates-update",
        coordinatesSubscribers.map { it.toFunction() },
        consumerPropertiesFactory.create(CollaboratorCoordinatesDeserializer::class.java)
    )

    fun consumeAbsent() = consumer.consume(
        "collaborator-coordinates-absent",
        coordinatesSubscribers.map { subscriber -> { id: String -> subscriber.notifyAbsent(id) } },
        consumerPropertiesFactory.create(StringDeserializer::class.java)
    )

    private fun CollaboratorCoordinatesSubscriber.toFunction() = {
        pair: Pair<String, Coordinates> -> this.notify(pair.first, pair.second)
    }
}
