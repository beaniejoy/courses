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
