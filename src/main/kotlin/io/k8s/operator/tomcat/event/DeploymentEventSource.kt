package io.k8s.operator.tomcat.event

import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.Watcher
import io.fabric8.kubernetes.client.WatcherException
import io.javaoperatorsdk.operator.processing.KubernetesResourceUtils.getUID
import io.javaoperatorsdk.operator.processing.KubernetesResourceUtils.getVersion
import io.javaoperatorsdk.operator.processing.event.AbstractEventSource
import mu.KotlinLogging
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class DeploymentEventSource(val kubernetesClient: KubernetesClient) : AbstractEventSource(), Watcher<Deployment> {

    @PostConstruct
    fun registerWatch() {
        log.info { "Registering watcher for Kubernetes Deployment resources" }
        kubernetesClient
                .apps()
                .deployments()
                .inAnyNamespace()
                .withLabel("app.kubernetes.io/managed-by", "tomcat-operator")
                .watch(this)
    }

    override fun eventReceived(action: Watcher.Action, deployment: Deployment) {
        log.debug("Event received for action: {}, Deployment: {} (ReadyReplicas={})", action.name, deployment.metadata.name, deployment.status.readyReplicas)

        if (action == Watcher.Action.ERROR) {
            log.warn("Skipping {} event for custom resource uuid: {}, version {}", action, getUID(deployment), getVersion(deployment))
            return
        }

        eventHandler.handleEvent(DeploymentEvent(action, deployment, this))
    }

    override fun onClose(exception: WatcherException?) {
        if (exception == null) return

        if (exception.isHttpGone) {
            log.warn("Received error for watch, will try to reconnect.", exception)
            registerWatch()
        } else {
            // Note that this should not happen normally, since fabric8 client handles reconnect.
            // In case it tries to reconnect this method is not called.
            log.error("Unexpected error happened with watch. Will exit.", exception)
            Runtime.getRuntime().exit(1)
        }
    }
}