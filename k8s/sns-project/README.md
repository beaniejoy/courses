# k8s sns project example

<br>

## AWS 관련 설정

```shell
kubectl run mysql-client --image=mysql:8 -it --rm -- bash
```

- `--rm`:  pod 사용 끝나자마자 삭제

<br>

## telepresence

로컬 환경에서 쿠베 애플리케이션 개발할 때 사용되는 도구
https://www.telepresence.io/docs/latest/quick-start

```shell
telepresence helm install

telepresence connect
```

kubectl context 연결되어 있는 cluster에 telepresence를 설치해준다.
이렇게 되면 원래 해당 pod에 직접 bash 접속해서 curl localhost 호출하는 방식으로 했다면
telepresence 설치하면 service의 클러스터 내부 도메인으로 curl 호출이 가능해 진다.

```shell
curl -v 'http://feed-service.sns.svc.cluster.local:8080/healthcheck/live'
```

