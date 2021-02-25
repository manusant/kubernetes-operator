package io.k8s.operator.tomcat

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import mu.KotlinLogging
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class ApplicationLifecycle {

    fun onStart(@Observes event: StartupEvent?) {
        log.info { "Tomcat K8s Operator successfully started" }
    }

    fun onStop(@Observes event: ShutdownEvent?) {
        log.info { "Tomcat K8s Operator successfully stopped" }
    }
}