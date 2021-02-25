package io.k8s.operator.tomcat.operator

import io.javaoperatorsdk.operator.Operator
import mu.KotlinLogging
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class TomcatOperator {

    @Inject
    lateinit var operator: Operator

    @PostConstruct
    fun registerControllers() {
        log.debug { "Registering controllers to Tomcat Operator" }

    }
}