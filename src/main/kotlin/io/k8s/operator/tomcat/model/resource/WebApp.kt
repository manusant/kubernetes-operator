package io.k8s.operator.tomcat.model.resource

import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import io.k8s.operator.tomcat.model.spec.WebAppSpec
import io.k8s.operator.tomcat.model.status.WebAppStatus

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@Group("tomcat.operator.io")
@Version("v1")
class WebApp : CustomResource<WebAppSpec, WebAppStatus>()