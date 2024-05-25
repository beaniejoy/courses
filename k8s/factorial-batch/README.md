# factorial batch for k8s

<br>

## batch 과정 모니터링

```shell
watch -n 1 kubectl exec redis -- redis-cli scard factorial:task-queue
```