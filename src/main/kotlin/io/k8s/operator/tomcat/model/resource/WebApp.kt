package io.k8s.operator.tomcat.model.resource

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import io.k8s.operator.tomcat.model.spec.WebAppSpec
import io.k8s.operator.tomcat.model.status.WebAppStatus
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@RegisterForReflection
@Group("tomcat.operator.io")
@Version("v1")
class WebApp(var spec: WebAppSpec = WebAppSpec(), var status: WebAppStatus = WebAppStatus()) : CustomResource<WebAppSpec, WebAppStatus>(), Namespaced {

    override fun initSpec(): WebAppSpec {
        return WebAppSpec()
    }

    override fun initStatus(): WebAppStatus {
        return WebAppStatus()
    }
}