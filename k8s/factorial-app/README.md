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

<br>

## opensearch 

elasticsearch에서 fork되어 나온 오픈소스 (로그 저장소)

```shell
helm repo add opensearch https://opensearch-project.github.io/helm-charts/
```

elasticsearch 같은 로그 저장소 설치
```shell
kubectl create namespace opensearch
helm -n opensearch install --values=opensearch/values.yaml opensearch opensearch/opensearch
```
(OPENSEARCH_INITIAL_ADMIN_PASSWORD 이슈로 따로 지정한 values.yaml로 install)

kibana 같은 로그 대시보드 설치
```shell
helm -n opensearch install dashboard opensearch/opensearch-dashboards
```

대시보드 port forwarding 
```shell
kubectl -n opensearch port-forward dashboard-opensearch-dashboards-65b6c8749d-4dqcx 56
01 
```

- fluent-bit

```
Logstash_Format On
Logstash_Prefix factorial-app-logs
```
prefix 뒤에 날짜 형식이 붙어서 저장
이렇게 되면 훗날 과거 로그 내역 삭제하기가 간편해짐