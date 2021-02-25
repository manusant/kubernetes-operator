package io.k8s.operator.tomcat.event

import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.client.Watcher
import io.javaoperatorsdk.operator.processing.event.AbstractEvent

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
class DeploymentEvent(val action: Watcher.Action, val deployment: Deployment, deploymentEventSource: DeploymentEventSource)
    : AbstractEvent(deployment.metadata.ownerReferences[0].uid, deploymentEventSource) {

    fun resourceUid(): String {
        return deployment.metadata.uid
    }

    override fun toString(): String {
        return ("CustomResourceEvent{"
                + "action="
                + action
                + ", resource=[ name="
                + deployment.metadata.name
                + ", kind="
                + deployment.kind
                + ", apiVersion="
                + deployment.apiVersion
                + " ,resourceVersion="
                + deployment.metadata.resourceVersion
                + ", markedForDeletion: "
                + (deployment.metadata.deletionTimestamp != null
                && deployment.metadata.deletionTimestamp.isNotEmpty())
                + " ]"
                + '}')
    }
}
