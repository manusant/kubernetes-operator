package io.k8s.operator.tomcat

import io.javaoperatorsdk.operator.Operator
import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import mu.KotlinLogging
import javax.inject.Inject

private val log = KotlinLogging.logger {}

@QuarkusMain
class OperatorApplication : QuarkusApplication {

    @Inject
    lateinit var operator: Operator

    override fun run(vararg args: String?): Int {
        log.info { "Starting Tomcat K8s Operator..." }
        operator.start();
        Quarkus.waitForExit();
        return 0;
    }
}

fun main(args: Array<String>) {
    Quarkus.run(OperatorApplication::class.java, *args)
}