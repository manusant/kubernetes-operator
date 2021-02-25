@file:JvmName("TomcatDeploymentServiceKt")

package io.k8s.operator.tomcat.service

import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.RollableScalableResource
import io.k8s.operator.tomcat.model.resource.Tomcat
import io.k8s.operator.tomcat.model.status.TomcatStatus
import mu.KotlinLogging
import java.util.*
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class DeploymentService(val kubernetesClient: KubernetesClient, val resourcesService: ResourcesService) {

    fun find(namespace: String, name: String): RollableScalableResource<Deployment> {
        log.debug("Searching Deployment {} from namespace {}", name, namespace)
        return kubernetesClient.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(name)
    }

    fun updateStatus(tomcat: Tomcat, deployment: Deployment): Tomcat {
        log.debug("Updating status for Deployment {} in namespace {}", deployment.metadata.name, deployment.metadata.namespace)

        val deploymentStatus = Objects.requireNonNullElse(deployment.status, DeploymentStatus())
        val readyReplicas = Objects.requireNonNullElse(deploymentStatus.readyReplicas, 0)

        val status = TomcatStatus()
        status.readyReplicas = readyReplicas
        tomcat.status = status
        return tomcat
    }

    fun createOrUpdate(tomcat: Tomcat): Deployment {
        log.debug("Creating or Updating Deployment {} in namespace {}", tomcat.metadata.name, tomcat.metadata.namespace)

        val existingDeployment = find(tomcat.metadata.namespace, tomcat.metadata.name)

        return if (existingDeployment.get() == null) {
            create(tomcat)
        } else {
            update(tomcat, existingDeployment.get())
        }
    }

    fun create(tomcat: Tomcat): Deployment {

        val deployment = resourcesService.loadYaml(Deployment::class.java, "deployment.yaml")

        val namespace = tomcat.metadata.namespace
        val name = tomcat.metadata.name

        deployment.metadata.name = name
        deployment.metadata.namespace = namespace
        deployment.metadata.labels["app.kubernetes.io/part-of"] = name
        deployment.metadata.labels["app.kubernetes.io/managed-by"] = "tomcat-operator"

        // set tomcat version
        deployment.spec.template.spec.containers[0].image = "tomcat:" + tomcat.spec.version

        deployment.spec.replicas = tomcat.spec.replicas

        // make sure label selector matches label (which has to be matched by service selector too)
        deployment.spec.template.metadata.labels["app"] = name
        deployment.spec.selector.matchLabels["app"] = name

        val ownerReference = deployment.metadata.ownerReferences[0]
        ownerReference.name = name
        ownerReference.uid = tomcat.metadata.uid

        log.info("Creating Deployment {} in namespace {}", name, namespace)
        return kubernetesClient.apps()
                .deployments()
                .inNamespace(namespace)
                .create(deployment)
    }

    fun update(tomcat: Tomcat, existingDeployment: Deployment): Deployment {
        log.info("Updating Deployment {} in namespace {}", tomcat.metadata.name, tomcat.metadata.namespace)

        existingDeployment.spec.template.spec.containers[0].image = "tomcat:" + tomcat.spec.version

        return kubernetesClient.apps()
                .deployments()
                .inNamespace(tomcat.metadata.namespace)
                .createOrReplace(existingDeployment)
    }

    fun delete(tomcat: Tomcat) {
        log.info("Deleting Deployment {} in namespace {}", tomcat.metadata.name, tomcat.metadata.namespace)

        val existingDeployment = find(tomcat.metadata.namespace, tomcat.metadata.name)

        if (existingDeployment.get() != null) {
            existingDeployment.delete()
        }
    }
}