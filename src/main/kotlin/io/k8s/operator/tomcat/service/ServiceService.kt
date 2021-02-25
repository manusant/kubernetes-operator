package io.k8s.operator.tomcat.service

import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.ServiceResource
import io.k8s.operator.tomcat.model.resource.Tomcat
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class ServiceService(val kubernetesClient: KubernetesClient, val resourcesService: ResourcesService) {

    fun find(namespace: String, name: String): ServiceResource<Service> {
        log.debug("Searching Service {} on namespace {}", name, namespace)
        return kubernetesClient.services()
                .inNamespace(namespace)
                .withName(name)
    }

    fun createOrUpdate(tomcat: Tomcat) {
        log.debug { "Loading service definitions fromm yaml" }
        val service = resourcesService.loadYaml(Service::class.java, "service.yaml")

        val namespace = tomcat.metadata.namespace
        val name = tomcat.metadata.name

        service.metadata.name = name
        service.metadata.namespace = namespace
        service.spec.selector["app"] = name

        log.debug("Creating or updating Service {} in {}", name, namespace)
        kubernetesClient.services()
                .inNamespace(namespace)
                .createOrReplace(service)
    }

    fun delete(tomcat: Tomcat) {
        log.debug("Deleting Service {}", tomcat.metadata.name)

        val service = find(tomcat.metadata.namespace, tomcat.metadata.name)

        if (service.get() != null) {
            service.delete()
        }
    }
}