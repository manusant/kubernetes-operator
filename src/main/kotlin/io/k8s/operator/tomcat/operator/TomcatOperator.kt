package io.k8s.operator.tomcat.operator

import io.javaoperatorsdk.operator.Operator
import io.k8s.operator.tomcat.controller.TomcatController
import mu.KotlinLogging
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped

private val log = KotlinLogging.logger {}

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@ApplicationScoped
class TomcatOperator(val operator: Operator, val tomcatController: TomcatController) {

    @PostConstruct
    fun registerControllers() {
        log.info { "Registering controllers to Tomcat Operator" }

        log.info { "Registering TomcatController" }
        operator.register(tomcatController)
    }
}