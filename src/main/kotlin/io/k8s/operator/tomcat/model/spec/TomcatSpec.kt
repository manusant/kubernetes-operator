package io.k8s.operator.tomcat.model.spec

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
data class TomcatSpec(var version: Int, var replicas: Int) {

    // Default constructor required by Operator SDK
    constructor() : this(0, 0)
}
