package lt.boldadmin.nexus.plugin.eventkafka.kafka.consumer

import lt.boldadmin.nexus.api.event.subscriber.CollaboratorLocationSubscriber
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.eventkafka.kafka.deserializer.CollaboratorCoordinatesDeserializer
import lt.boldadmin.nexus.plugin.eventkafka.kafka.factory.ConsumerPropertiesFactory

class LogWorkByLocationConsumer(
    private val consumerPropertiesFactory: ConsumerPropertiesFactory,
    private val consumer: Consumer,
    private val service: CollaboratorLocationSubscriber
) {
    fun consume() = consumer.consume<Pair<String, Coordinates>>(
        "collaborator-location-update-by-coordinates",
        { service.logWork(it.first, it.second) },
        consumerPropertiesFactory.create(CollaboratorCoordinatesDeserializer::class.java)
    )//TODO this should be in nexus, and this plugin should fetch this as a function
}