# factorial app for k8s

## Gradle jib

```kotlin
id("com.google.cloud.tools.jib") version "3.4.2"
```

<br>

## kube config

```shell
kubectl create namespace factorial
```

<br>

## HPA autoscaling 테스트 관련

```shell
kubectl -n factorial edit ingress factorial-ingress
```
기존 factorial-ingress를 잠시 factorial-app을 바라보게끔 바꿔둔다.

<br>

처리량이 못따라오는 경우 replicas 조절하면 된다.
```shell
kubectl -n factorial scale deployment my-factorial-app --replicas=4
```