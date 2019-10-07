package lt.boldadmin.nexus.plugin.kafka.factory

import org.slf4j.LoggerFactory

object LoggerFactory {
    fun <T>create(clazz: Class<T>) = LoggerFactory.getLogger(clazz)!!
}