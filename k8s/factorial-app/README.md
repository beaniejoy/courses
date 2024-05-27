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

<br>

## Resource 제한

```shell
kubectl -n factorial describe resourcequota factorial-quota
```

<br>

## Helm 

```shell
helm create factorial-chart
```
해당 경로에 factorial-chart 디렉토리안에 helm chart 관련 파일들 생성됨

```shell
helm -n factorial install my-test-app factorial-chart/

helm -n factorial uninstall my-test-app
```

스펙 변경 후
```shell
helm -n factorial upgrade my-test-app factorial-chart --values factorial-chart/values.yaml
```
Revision history 리스트 확인
```shell
helm -n factorial history my-test-app
```
롤백
```shell
helm -n factorial rollback my-test-app [REVISION_NUMBER]
```
