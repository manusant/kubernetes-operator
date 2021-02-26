package io.k8s.operator.tomcat.service

import io.fabric8.kubernetes.api.model.PodList
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.ExecWatch
import io.fabric8.kubernetes.client.utils.Utils
import mu.KotlinLogging
import java.io.ByteArrayOutputStream
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class PodService(val kubernetesClient: KubernetesClient) {

    fun list(namespace: String, matchLabels: Map<String, String>): PodList {
        log.debug("Listing Pods matching labels {} on namespace {}", matchLabels, namespace)

        return kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabels(matchLabels)
                .list()
    }

    fun exec(namespace: String, podName: String, container: String, command: Array<String>): ExecWatch {
        log.info("Executing command {} in Pod {} in namespace {}", Utils.join(command, ' '), podName, namespace)

        return kubernetesClient.pods()
                .inNamespace(namespace)
                .withName(podName)
                .inContainer(container)
                .writingOutput(ByteArrayOutputStream())
                .writingError(ByteArrayOutputStream())
                .exec(*command)
    }
}