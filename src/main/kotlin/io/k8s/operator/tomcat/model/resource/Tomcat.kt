package io.k8s.operator.tomcat.model.resource

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Version
import io.k8s.operator.tomcat.model.spec.TomcatSpec
import io.k8s.operator.tomcat.model.status.TomcatStatus

/**
 * @author manusant (ney.br.santos@gmail.com)
 *
 * */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
//@JsonSubTypes(JsonSubTypes.Type(value = TomcatSpec::class, name = "spec"), JsonSubTypes.Type(value = TomcatStatus::class, name = "status"))
@Group("tomcat.operator.io")
@Version("v1")
class Tomcat(var spec: TomcatSpec = TomcatSpec(), var status: TomcatStatus = TomcatStatus()) : CustomResource<TomcatSpec, TomcatStatus>(), Namespaced