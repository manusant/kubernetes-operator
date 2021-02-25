package io.k8s.operator.tomcat.controller

import io.javaoperatorsdk.operator.api.*
import io.javaoperatorsdk.operator.processing.event.EventSourceManager
import io.k8s.operator.tomcat.event.DeploymentEventSource
import io.k8s.operator.tomcat.model.resource.Tomcat
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

@Controller
@ApplicationScoped
class TomcatController(val deploymentEventSource: DeploymentEventSource) : ResourceController<Tomcat> {

    override fun init(eventSourceManager: EventSourceManager) {
        eventSourceManager.registerEventSource("deployment-event-source", this.deploymentEventSource)
    }

    override fun createOrUpdateResource(tomcat: Tomcat, context: Context<Tomcat>): UpdateControl<Tomcat> {
        log.debug { "Creating or Updating Tomcat Resource" }
        TODO("Not yet implemented")
    }

    override fun deleteResource(tomcat: Tomcat, context: Context<Tomcat>): DeleteControl {
        log.debug { "Deleting Tomcat Resource" }
        TODO("Not yet implemented")
    }
}