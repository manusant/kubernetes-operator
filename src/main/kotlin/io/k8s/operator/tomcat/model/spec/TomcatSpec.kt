package io.k8s.operator.tomcat.model.spec

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
data class TomcatSpec(var version: Int = 0, var port: Int = 0, var replicas: Int = 0)
