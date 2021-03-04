package io.k8s.operator.tomcat.model.resource

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import io.k8s.operator.tomcat.model.spec.TomcatSpec
import io.k8s.operator.tomcat.model.status.TomcatStatus
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
@RegisterForReflection
@Group("tomcat.operator.io")
@Version("v1")
class Tomcat(var spec: TomcatSpec = TomcatSpec(), var status: TomcatStatus = TomcatStatus()) : CustomResource<TomcatSpec, TomcatStatus>(), Namespaced {

    override fun initSpec(): TomcatSpec {
        return TomcatSpec()
    }

    override fun initStatus(): TomcatStatus {
        return TomcatStatus()
    }
}
