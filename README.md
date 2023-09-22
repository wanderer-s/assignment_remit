# 목표
가입자간 송금을 할 수 있는 API 설계/구현

# 상세정의
아래 명시된 api를 http로 설계 및 구현
- 송금 API
- 특정 계좌의 송금 목록을 볼 수 있는 API

# 요구사항
- Java/Kotlin으로 개발
- 사용자는 가입 상태이거나 탈퇴 일 수 있음
- 사용자마다 보유할 수 있는 금액의 최대 한도가 있고, 서로 다를 수 있음
  - 한도가 넘을 경우 해당 송금은 실패해야 함
- 송금의 취소는 고려하지 않음
- 사용자 인증은 parameter 사용자 id로 대체

# EndPoint
인가를 위한 `user/{userId}` 가 후미에 붙어있음
ex **POST** *api/wallet/{id}/deposit/user/{userId}*
## api/account
**GET /{id}**
- parameter - id: Long
```json lines
// return
// AccountResponse
  {
  "id": Long,
  "userId": Long,
  "balance": BigDecimal,
  "depositLimit": BigDecimal,
  "status": AccountStatus, // ACTIVE, INACTIVE
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
  }
```
**PATCH /deposit**
- Request Body: DepositRequest
```json lines
{
  "amount" : BigDecimal, // 0보다 크지 않으면 error
  "memo": String?
}
```
**PATCH /withdraw**
- Request Body: WithdrawRequest
```json lines
{
  "amount" : BigDecimal, // 0보다 크지 않으면 error
  "memo": String?
}
```
**PATCH /transfer**
- Request Body: TransferRequest
```json lines
{
  "depositAccountId": Long,
  "amount": BigDecimal, // 0보다 크지 않으면 error
  "memo": String?
}
```
## api/transaction
**GET**
- Query Param
```json lines
{
  "year" : Int,
  "month": Int, // 1~12 사이를 벗어나면 error
  "accountId": Long,
  "status": TransactionFilterStatus? // INCOME, SPEND
  }
```
- return: TransactionResponses
```json lines
[
  {
    "id": Long,
    "uuid": UUID,
    "withdrawalAccountId": Long?,
    "depositAccountId": Long?,
    "amount": BigDecimal,
    "type": TransactionType, // DEPOSIT, WITHDRAW, TRANSFER
    "status": TransactionStatus, // SUCCESS, PENDING, FAIL
    "memo": String?,
    "createdAt": LocalDateTime
  }
]
```
