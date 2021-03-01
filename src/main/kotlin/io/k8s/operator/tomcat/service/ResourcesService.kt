package io.k8s.operator.tomcat.service

import io.fabric8.kubernetes.client.utils.Serialization
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class ResourcesService {

    fun <T> loadYaml(clazz: Class<T>, yaml: String): T {
        log.debug { "Loading YAML file to Resource" }
        try {
            javaClass.getResourceAsStream("/k8s/$yaml").use { inputStream -> return Serialization.unmarshal(inputStream, clazz) }
        } catch (ex: Exception) {
            throw IllegalStateException("Cannot find yaml on classpath: $yaml", ex)
        }
    }
}