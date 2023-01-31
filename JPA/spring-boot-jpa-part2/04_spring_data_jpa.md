# Spring Data JPA overview

## 궁금한 내용
- N+1 문제 발생 상황 중
  - 연관관계 entity fetch 전략이 EAGER로 묶인 Member - Order 관계
- Member를 Spring Data JPA 스펙 사용해서 `findById` 조회
  - 이 상황에서는 N+1 문제 발생 X
- Member를 JPQL 직접 이용해 조회
  - 이 상황에서는 N+1 문제 발생
- 두 상황이 어떤 차이가 있는 것인지