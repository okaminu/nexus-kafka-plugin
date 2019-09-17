package lt.boldadmin.nexus.plugin.kafka.event.publisher

import lt.boldadmin.nexus.api.event.publisher.CollaboratorCoordinatesPublisher
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaProducerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.ProducerPropertiesFactory
import lt.boldadmin.nexus.plugin.kafka.serializer.CollaboratorCoordinatesSerializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class CollaboratorCoordinatesPublisher(
    private val producerFactory: KafkaProducerFactory,
    private val producerPropertiesFactory: ProducerPropertiesFactory
): CollaboratorCoordinatesPublisher {

    override fun publish(collaboratorId: String, coordinates: Coordinates) {
        val properties = producerPropertiesFactory.create(CollaboratorCoordinatesSerializer::class.java)
        val producer = producerFactory.create<Pair<String, Coordinates>>(properties)
        producer.send(ProducerRecord("collaborator-coordinates-update", Pair(collaboratorId, coordinates)))
    }

    override fun publishAbsent(collaboratorId: String) {
        val properties = producerPropertiesFactory.create(StringSerializer::class.java)
        val producer = producerFactory.create<String>(properties)
        producer.send(ProducerRecord("collaborator-coordinates-absent", collaboratorId))
    }
}
