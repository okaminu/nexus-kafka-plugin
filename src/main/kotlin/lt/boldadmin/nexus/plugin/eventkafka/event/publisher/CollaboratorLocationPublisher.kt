package lt.boldadmin.nexus.plugin.eventkafka.event.publisher

import lt.boldadmin.nexus.api.event.publisher.CollaboratorLocationPublisher
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.eventkafka.factory.KafkaProducerFactory
import lt.boldadmin.nexus.plugin.eventkafka.factory.ProducerPropertiesFactory
import lt.boldadmin.nexus.plugin.eventkafka.serializer.CollaboratorCoordinatesSerializer
import lt.boldadmin.nexus.plugin.eventkafka.serializer.CollaboratorMessageSerializer
import org.apache.kafka.clients.producer.ProducerRecord

class CollaboratorLocationPublisher(
    private val producerFactory: KafkaProducerFactory,
    private val producerPropertiesFactory: ProducerPropertiesFactory
): CollaboratorLocationPublisher {

    override fun publish(collaboratorId: String, coordinates: Coordinates) {
        val properties = producerPropertiesFactory.create(CollaboratorCoordinatesSerializer::class.java)
        val producer = producerFactory.create<Pair<String, Coordinates>>(properties)
        producer.send(ProducerRecord("collaborator-location-update-by-coordinates", Pair(collaboratorId, coordinates)))
    }

    override fun publish(message: Message) {
        val properties = producerPropertiesFactory.create(CollaboratorMessageSerializer::class.java)
        val producer = producerFactory.create<Message>(properties)
        producer.send(ProducerRecord("collaborator-location-update-by-message", message))
    }
}