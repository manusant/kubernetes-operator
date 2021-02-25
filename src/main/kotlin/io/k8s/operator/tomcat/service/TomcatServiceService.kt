package io.k8s.operator.tomcat.service

import io.k8s.operator.tomcat.model.resource.Tomcat
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

@ApplicationScoped
class TomcatServiceService(val resourcesService: ResourcesService) {

    public fun createOrUpdateService(tomcat: Tomcat) {

    }

    public fun deleteService(tomcat: Tomcat) {

    }
}