# factorial cache app for k8s

## Config

secret은 따로 파일로 github repo에 올리지 않는 것을 추천

```shell
kubectl -n factorial create secret generic factorial-secret --from-literal=api-key=abcd-1234-efgh
```
value 부분이 base64 encoding 되어서 저장된다.

```shell
kubectl -n factorial get secret factorial-secret -o yaml
echo "YWJjZC0xMjM0LWVmZ2g=" | base64 -d
```
base64 encoding 된 value 값을 확인 가능

```shell
echo -n "abcd-1234-efgh" | base64
```
직접 secret에 값을 넣을 때 base64 encoding이 필요할텐데 개행에 주의해서 n option 넣어야 함

```shell
kubectl -n factorial edit configmap factorial-config
kubectl -n factorial rollout restart deployments my-factorial-cache-app
```
위와 같이 콘솔에서 configmap 내용 변경을 할 수 있다.
configmap 내용만 변경했다고 deployment에 반영되는 것이 아니라서 restart 해줘야 함 (rollout restart 해줌)