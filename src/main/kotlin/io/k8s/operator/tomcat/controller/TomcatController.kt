package io.k8s.operator.tomcat.controller

import io.javaoperatorsdk.operator.api.*
import io.javaoperatorsdk.operator.processing.event.EventSourceManager
import io.javaoperatorsdk.operator.processing.event.internal.CustomResourceEvent
import io.k8s.operator.tomcat.event.DeploymentEvent
import io.k8s.operator.tomcat.event.DeploymentEventSource
import io.k8s.operator.tomcat.model.resource.Tomcat
import io.k8s.operator.tomcat.service.TomcatDeploymentService
import io.k8s.operator.tomcat.service.TomcatServiceService
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@Controller
@ApplicationScoped
class TomcatController(val deploymentEventSource: DeploymentEventSource, val deploymentService: TomcatDeploymentService, val serviceService: TomcatServiceService)
    : ResourceController<Tomcat> {

    override fun init(eventSourceManager: EventSourceManager) {
        log.info { "Initializing Tomcat Controller" }
        eventSourceManager.registerEventSource("deployment-event-source", this.deploymentEventSource)
    }

    override fun createOrUpdateResource(tomcat: Tomcat, context: Context<Tomcat>): UpdateControl<Tomcat> {
        log.debug { "Create or Update event received for Tomcat Resource" }

        val latestCustomResourceEvent = context.events.getLatestOfType(CustomResourceEvent::class.java)
        if (latestCustomResourceEvent.isPresent) {
            deploymentService.createOrUpdate(tomcat)
            serviceService.createOrUpdate(tomcat)
        }

        val latestDeploymentEvent = context.events.getLatestOfType(DeploymentEvent::class.java)
        if (latestDeploymentEvent.isPresent) {

            val updatedTomcat = deploymentService.updateStatus(tomcat, latestDeploymentEvent.get().deployment)

            log.info("Updating status of Tomcat {} in namespace {} to {} ready replicas", tomcat.metadata.name, tomcat.metadata.namespace, tomcat.status.readyReplicas)
            return UpdateControl.updateStatusSubResource(updatedTomcat)
        }
        return UpdateControl.noUpdate()
    }

    override fun deleteResource(tomcat: Tomcat, context: Context<Tomcat>): DeleteControl {
        log.debug { "Delete event received for Tomcat Resource" }

        deploymentService.delete(tomcat)
        serviceService.delete(tomcat)
        return DeleteControl.DEFAULT_DELETE
    }
}