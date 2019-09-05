package lt.boldadmin.nexus.plugin.eventkafka.event.publisher

import lt.boldadmin.nexus.api.event.publisher.CollaboratorLocationPublisher
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.eventkafka.kafka.factory.KafkaProducerFactory
import lt.boldadmin.nexus.plugin.eventkafka.kafka.factory.ProducerPropertiesFactory
import lt.boldadmin.nexus.plugin.eventkafka.kafka.serializer.CollaboratorCoordinatesSerializer
import lt.boldadmin.nexus.plugin.eventkafka.kafka.serializer.MessageSerializer
import org.apache.kafka.clients.producer.ProducerRecord

class CollaboratorLocationPublisherAdapter(
    private val producerFactory: KafkaProducerFactory,
    private val producerPropertiesFactory: ProducerPropertiesFactory
): CollaboratorLocationPublisher {

    override fun publish(collaboratorId: String, coordinates: Coordinates) {
        producerFactory
            .create<Pair<String, Coordinates>>(producerPropertiesFactory.create(CollaboratorCoordinatesSerializer::class.java))
            .send(ProducerRecord("collaborator-location-update-by-coordinates", Pair(collaboratorId, coordinates)))
    }

    override fun publish(message: Message) {
        producerFactory
            .create<Message>(producerPropertiesFactory.create(MessageSerializer::class.java))
            .send(ProducerRecord("collaborator-location-update-by-message", message))
    }
}