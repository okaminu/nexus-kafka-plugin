package lt.boldadmin.nexus.plugin.eventkafka.kafka


class KafkaServerAddressProvider {
    val url get() = System.getenv("KAFKA_SERVER_URL") ?: throw KafkaServerAddressNotSetException
}