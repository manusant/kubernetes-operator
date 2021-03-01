package io.k8s.operator.tomcat.model.spec

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
data class WebAppSpec(var url: String = "", var contextPath: String = "", var tomcat: String = "")
