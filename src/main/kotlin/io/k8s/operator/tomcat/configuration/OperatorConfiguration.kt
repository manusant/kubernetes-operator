package io.k8s.operator.tomcat.configuration

import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import io.fabric8.kubernetes.client.KubernetesClient
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces
import javax.inject.Singleton

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class OperatorConfiguration {

    @Produces
    @Singleton
    fun kubernetesClient(): KubernetesClient {
        log.debug { "Creating and configuring default Kubernetes client" }
        val config = Config.autoConfigure("minikube")
        return DefaultKubernetesClient(config)
    }
}