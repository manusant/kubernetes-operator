package io.k8s.operator.tomcat.model.status

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
data class TomcatStatus(var readyReplicas: Int = 0, var error: String = "")
