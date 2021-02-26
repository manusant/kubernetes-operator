package io.k8s.operator.tomcat.controller

import io.javaoperatorsdk.operator.api.*
import io.k8s.operator.tomcat.model.resource.WebApp
import io.k8s.operator.tomcat.service.DeploymentService
import io.k8s.operator.tomcat.service.PodService
import mu.KotlinLogging
import java.util.*
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@Controller
@ApplicationScoped
class WebAppController(val podService: PodService, val deploymentService: DeploymentService) : ResourceController<WebApp> {

    override fun createOrUpdateResource(webApp: WebApp, context: Context<WebApp>): UpdateControl<WebApp> {
        log.debug { "Create or Update event received for WebApp Resource" }

        return if (!Objects.equals(webApp.spec.url, webApp.status.deployedArtifact)) {

            val command = arrayOf("wget", "-O", "/data/${webApp.spec.contextPath}.war", webApp.spec.url)

            execInAllPods(webApp, command)

            webApp.status.deployedArtifact = webApp.spec.url
            UpdateControl.updateStatusSubResource(webApp)
        } else {
            UpdateControl.noUpdate()
        }
    }

    override fun deleteResource(webApp: WebApp, context: Context<WebApp>): DeleteControl {
        log.debug { "Delete event received for WebApp Resource" }

        val command = arrayOf("rm", "/data/${webApp.spec.contextPath}.war")

        execInAllPods(webApp, command)

        return DeleteControl.DEFAULT_DELETE
    }

    private fun execInAllPods(webApp: WebApp, command: Array<String>) {

        val deployment = deploymentService.find(webApp.metadata.namespace, webApp.spec.tomcat).get()

        if (deployment != null) {

            val podsList = podService.list(webApp.metadata.namespace, deployment.spec.selector.matchLabels)

            podsList.items.forEach {
                podService.exec(deployment.metadata.namespace, it.metadata.name, "war-downloader", command)
            }
        }
    }
}