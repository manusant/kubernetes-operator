@file:JvmName("TomcatDeploymentServiceKt")

package io.k8s.operator.tomcat.service

import io.fabric8.kubernetes.api.model.apps.Deployment
import io.k8s.operator.tomcat.model.resource.Tomcat
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

@ApplicationScoped
class TomcatDeploymentService {

    public fun updateTomcatStatus(tomcat: Tomcat, deployment: Deployment): Tomcat {
        return Tomcat()
    }

    public fun createOrUpdateDeployment(tomcat: Tomcat) {

    }

    public fun deleteDeployment(tomcat: Tomcat) {

    }
}