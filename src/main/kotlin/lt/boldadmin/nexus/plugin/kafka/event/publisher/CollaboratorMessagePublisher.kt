package lt.boldadmin.nexus.plugin.kafka.event.publisher

import lt.boldadmin.nexus.api.event.publisher.CollaboratorMessagePublisher
import lt.boldadmin.nexus.api.type.valueobject.Message
import lt.boldadmin.nexus.plugin.kafka.factory.KafkaProducerFactory
import lt.boldadmin.nexus.plugin.kafka.factory.ProducerPropertiesFactory
import lt.boldadmin.nexus.plugin.kafka.serializer.CollaboratorMessageSerializer
import org.apache.kafka.clients.producer.ProducerRecord

class CollaboratorMessagePublisher(
    private val producerFactory: KafkaProducerFactory,
    private val producerPropertiesFactory: ProducerPropertiesFactory
): CollaboratorMessagePublisher {

    override fun publish(message: Message) {
        val properties = producerPropertiesFactory.create(CollaboratorMessageSerializer::class.java)
        val producer = producerFactory.create<Message>(properties)
        producer.send(ProducerRecord("collaborator-location-update-by-message", message))
    }
}
