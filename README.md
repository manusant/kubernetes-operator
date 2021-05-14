# Kubernetes Operator
Kubernetes Operator to manage Tomcat deployments

* Learning Quarkus
* Learning Kubernetes Operator development
* Learning Kotlin

> Works with java >= 11. In case you have other projects running a different java version please use ASDF with Java plugin.
```
$ asdf install java openjdk-14.0.1
$ asdf global java openjdk-14.0.1
```

## Problems
I found 3 issues when dealing with Kotlin data classes:
* Data classes representing *CustomResource* definitions should be explicitly registered for reflection using *@RegisterForReflection* annotation.
* A default constructor or default values for all arguments is required (this is required while deserializing kubernetes events into objects)
* Quarkus native build doesn't recognize the generic typed arguments in the *CustomResource*, so we need to explicitly provide initialization for the *spec* and *status*
classes by overriding *initSpec()* and *initStatus()* respectively.

A final Data class representing a *CustomResource* will look like:
```kotlin
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
```
## Native build
[Official documentation](https://quarkus.io/guides/building-native-image)
### Install GraalVM
https://www.graalvm.org/docs/getting-started/#install-graalvm
>  [Tutorial for Macc](https://blog.softwaremill.com/graalvm-installation-and-setup-on-macos-294dd1d23ca2)

Export environment variables:
```
export GRAALVM_HOME=$HOME/Library/Java/JavaVirtualMachines/<graalvm-dir>/Contents/Home
export PATH=$GRAALVM_HOME/bin:$PATH
```
### Install native-image
```
gu install native-image
```
### Software Notarization
If you are using macOS Catalina and later you may need to remove the quarantine attribute from the bits before you can use them. To do this, run the following:
```
sudo xattr -r -d com.apple.quarantine $HOME/Library/Java/JavaVirtualMachines/<graalvm-dir>
```

### Build
* Build native executable
```
./mvnw package -Pnative
```
* Creating a Linux executable without GraalVM installed
```
./mvnw package -Pnative -Dquarkus.native.container-build=true
```
> By default Quarkus automatically detects the container runtime. If you want to explicitely select the container runtime, you can do it with:
```
# Docker
./mvnw package -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=docker
# Podman
./mvnw package -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman
```
* Creating a container 

By far the easiest way to create a container-image from your Quarkus application is to leverage one of the container-image extensions.

If one of those extensions is present, then creating a container image for the native executable is essentially a matter of executing a single command:
```
./mvnw package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
```
> **quarkus.native.container-build=true** allows for creating a Linux executable without GraalVM being installed (and is only necessary if you don’t have GraalVM installed locally or your local operating system is not Linux)

> **quarkus.native.container-build=true** allows for creating a Linux executable without GraalVM being installed (and is only necessary if you don’t have GraalVM installed locally or your local operating system is not Linux)

### Test native build
* Running native executable directly
```
./tomcat-operator-1.0.0-SNAPSHOT-runner
```
