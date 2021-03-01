package io.k8s.operator.tomcat.controller

import io.fabric8.kubernetes.client.utils.Utils
import io.javaoperatorsdk.operator.api.*
import io.javaoperatorsdk.operator.processing.event.EventSourceManager
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

    override fun init(eventSourceManager: EventSourceManager) {
        log.info { "Initializing WebApp Controller" }
    }

    override fun createOrUpdateResource(webApp: WebApp, context: Context<WebApp>): UpdateControl<WebApp> {
        log.debug { "Create or Update event received for WebApp Resource" }

        try {
            return if (!Objects.equals(webApp.spec.url, webApp.status.deployedArtifact)) {

                val command = arrayOf("wget", "-O", "/data/${webApp.spec.contextPath}.war", webApp.spec.url)

                execInAllPods(webApp, command)

                webApp.status.deployedArtifact = webApp.spec.url

                log.debug("WebApp {} successfully installed in Tomcat {} in namespace {}", webApp.metadata.name, webApp.spec.tomcat, webApp.metadata.namespace)
                UpdateControl.updateStatusSubResource(webApp)
            } else {
                log.debug("Update skipped for WebApp {} in namespace {}", webApp.metadata.name, webApp.metadata.namespace)
                UpdateControl.noUpdate()
            }
        } catch (ex: Exception) {
            webApp.status.error = "Error creating/updating resource: ${ex.message}"
            throw ex
        }
    }

    override fun deleteResource(webApp: WebApp, context: Context<WebApp>): DeleteControl {
        log.debug { "Delete event received for WebApp Resource" }

        try {
            val command = arrayOf("rm", "/data/${webApp.spec.contextPath}.war")

            execInAllPods(webApp, command)

            log.debug("WebApp {} successfully deleted from Tomcat {} in namespace {}", webApp.metadata.name, webApp.spec.tomcat, webApp.metadata.namespace)
            return DeleteControl.DEFAULT_DELETE
        } catch (ex: Exception) {
            webApp.status.error = "Error deleting resource: ${ex.message}"
            throw ex
        }
    }

    private fun execInAllPods(webApp: WebApp, command: Array<String>) {
        log.debug("Start executing command {} in pods for Tomcat {} in namespace {}", Utils.join(command, ' '), webApp.spec.tomcat, webApp.metadata.namespace)

        val deployment = deploymentService.find(webApp.metadata.namespace, webApp.spec.tomcat).get()

        if (deployment != null) {

            val podsList = podService.list(webApp.metadata.namespace, deployment.spec.selector.matchLabels)

            podsList.items.forEach {
                podService.exec(deployment.metadata.namespace, it.metadata.name, "war-downloader", command)
            }
            log.debug("WebApp {} successfully installed in Tomcat {} in namespace {}", webApp.metadata.name, webApp.spec.tomcat, webApp.metadata.namespace)
        } else {
            val error = "Expected Tomcat deployment not found with name ${webApp.spec.tomcat} in namespace ${webApp.metadata.namespace}. Failed to configure WebApp ${webApp.metadata.name}."
            webApp.status.error = error
            log.error { error }
        }
    }
}