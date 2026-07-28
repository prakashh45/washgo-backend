# Payment Service Documentation

## Service Overview
- **Name**: payment-service
- **Port**: 8085
- **Base URL**: `/api/v1/payments`, `/api/v1/razorpay`
- **Package**: `com.washgo`
- **Database**: PostgreSQL `washgo_payment_db` (`jdbc:postgresql://localhost:5432/washgo_payment_db`)
- **Dependencies**: web, data-jpa, validation, security, actuator, eureka-client, config, openfeign, spring-kafka, postgresql, razorpay-java:1.4.8, springdoc-openapi, lombok

---

## 1. Create Payment
- **API Name & Overview**: Create Payment - Initializes a payment record for an order in the database with a PENDING status. Used by the checkout flow before actual payment processing.
- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/payments`
- **Full URL**: `https://api.washgo.in/api/v1/payments`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header Name | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "orderId": 101,
  "customerId": 201,
  "amount": 499.00,
  "paymentMethod": "UPI",
  "remarks": "Payment for order #101"
}
```
**Validation Rules**:
| Field | Type | Validation | Description |
|---|---|---|---|
| `orderId` | Long | `@NotNull` | The order ID this payment is for. |
| `customerId` | Long | `@NotNull` | The customer ID making the payment. |
| `amount` | BigDecimal | `@NotNull` | The payment amount. |
| `paymentMethod` | Enum (PaymentMethod) | `@NotNull` | CASH_ON_DELIVERY, UPI, CARD, NET_BANKING, WALLET |
| `remarks` | String | None | Optional remarks. |

### Success Response (201 Created)
```json
{
  "id": 1,
  "paymentNumber": "PAY-12345678",
  "orderId": 101,
  "customerId": 201,
  "amount": 499.00,
  "paymentMethod": "UPI",
  "paymentStatus": "PENDING",
  "gatewayTransactionId": null,
  "gatewayOrderId": null,
  "gatewayPaymentId": null,
  "remarks": "Payment for order #101",
  "createdAt": "2026-07-28T10:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Validation errors or BusinessException.
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment` (Table: `payments`)
- **Operations**: `existsByOrderId`, save new record.

### Service Flow
`PaymentController` -> `PaymentServiceImpl.createPayment` -> Checks if payment for order exists -> Generates `paymentNumber` -> Sets status to `PENDING` -> `PaymentRepository.save` -> Produces `PaymentCreatedEvent` to Kafka.

### Events
- **Kafka Producer**: Publishes `PaymentCreatedEvent` to `PAYMENT_CREATED` topic.

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{"orderId": 101, "customerId": 201, "amount": 499.00, "paymentMethod": "UPI", "remarks": "Payment for order #101"}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    orderId: 101,
    customerId: 201,
    amount: 499.00,
    paymentMethod: 'UPI',
    remarks: 'Payment for order #101'
  })
}).then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/payments', {
  orderId: 101,
  customerId: 201,
  amount: 499.00,
  paymentMethod: 'UPI',
  remarks: 'Payment for order #101'
}).then(response => console.log(response.data));
```

### Java Example
```java
// Using WebClient or RestTemplate
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
CreatePaymentRequest request = new CreatePaymentRequest(101L, 201L, new BigDecimal("499.00"), PaymentMethod.UPI, "Payment for order #101");
HttpEntity<CreatePaymentRequest> entity = new HttpEntity<>(request, headers);
ResponseEntity<PaymentResponse> response = restTemplate.postForEntity("https://api.washgo.in/api/v1/payments", entity, PaymentResponse.class);
```

### Notes
- Ensure `orderId` does not already have an active payment record to prevent duplicates.

---

## 2. Verify Payment
- **API Name & Overview**: Verify Payment - Marks a payment as SUCCESS using external gateway details.
- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/payments/verify`
- **Full URL**: `https://api.washgo.in/api/v1/payments/verify`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header Name | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "paymentNumber": "PAY-12345678",
  "gatewayPaymentId": "pay_xyz",
  "gatewayTransactionId": "txn_abc"
}
```
**Validation Rules**:
| Field | Type | Validation | Description |
|---|---|---|---|
| `paymentNumber` | String | `@NotBlank` | Unique generated payment number. |
| `gatewayPaymentId` | String | `@NotBlank` | Gateway payment ID. |
| `gatewayTransactionId` | String | `@NotBlank` | Gateway transaction ID. |

### Success Response (200 OK)
```json
{
  "id": 1,
  "paymentNumber": "PAY-12345678",
  "orderId": 101,
  "customerId": 201,
  "amount": 499.00,
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS",
  "gatewayTransactionId": "txn_abc",
  "gatewayOrderId": null,
  "gatewayPaymentId": "pay_xyz",
  "remarks": "Payment for order #101",
  "createdAt": "2026-07-28T10:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Validation errors or BusinessException.
- **404 Not Found**: ResourceNotFoundException (Payment not found).
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment`
- **Operations**: `findByPaymentNumber`, update `status`, `gatewayPaymentId`, `gatewayTransactionId`.

### Service Flow
`PaymentController` -> `PaymentServiceImpl.verifyPayment` -> Fetch payment -> Update fields -> Save -> Publish `PaymentSuccessEvent`.

### Events
- **Kafka Producer**: Publishes `PaymentSuccessEvent` to `PAYMENT_SUCCESS` topic.

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/payments/verify \
  -H "Content-Type: application/json" \
  -d '{"paymentNumber": "PAY-12345678", "gatewayPaymentId": "pay_xyz", "gatewayTransactionId": "txn_abc"}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments/verify', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    paymentNumber: "PAY-12345678",
    gatewayPaymentId: "pay_xyz",
    gatewayTransactionId: "txn_abc"
  })
}).then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/payments/verify', {
  paymentNumber: "PAY-12345678",
  gatewayPaymentId: "pay_xyz",
  gatewayTransactionId: "txn_abc"
}).then(response => console.log(response.data));
```

### Java Example
```java
VerifyPaymentRequest req = new VerifyPaymentRequest("PAY-12345678", "pay_xyz", "txn_abc");
HttpEntity<VerifyPaymentRequest> entity = new HttpEntity<>(req, headers);
restTemplate.postForEntity("https://api.washgo.in/api/v1/payments/verify", entity, PaymentResponse.class);
```

### Notes
- Requires the payment to exist in the database.

---

## 3. Refund Payment
- **API Name & Overview**: Refund Payment - Refunds a successful payment.
- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/payments/refund`
- **Full URL**: `https://api.washgo.in/api/v1/payments/refund`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header Name | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "paymentNumber": "PAY-12345678",
  "paymentId": 1,
  "reason": "Customer cancellation"
}
```
**Validation Rules**:
| Field | Type | Validation | Description |
|---|---|---|---|
| `paymentNumber` | String | `@NotBlank` | Unique generated payment number. |
| `paymentId` | Long | `@NotNull` | Database ID of the payment. |
| `reason` | String | None | Reason for refund. |

### Success Response (200 OK)
```json
{
  "id": 1,
  "paymentNumber": "PAY-12345678",
  "orderId": 101,
  "customerId": 201,
  "amount": 499.00,
  "paymentMethod": "UPI",
  "paymentStatus": "REFUNDED",
  "gatewayTransactionId": "txn_abc",
  "gatewayOrderId": null,
  "gatewayPaymentId": "pay_xyz",
  "remarks": "Customer cancellation",
  "createdAt": "2026-07-28T10:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Payment not in SUCCESS status.
- **404 Not Found**: Payment not found.
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment`
- **Operations**: Fetch by ID/number, Update status to REFUNDED.

### Service Flow
`PaymentController` -> `PaymentServiceImpl.refundPayment` -> Find Payment -> Validate status == SUCCESS -> Set REFUNDED -> Save.

### Events
- **Kafka Producer**: (Inferred: might publish RefundProcessedEvent, based on event schemas).

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/payments/refund \
  -H "Content-Type: application/json" \
  -d '{"paymentNumber": "PAY-12345678", "paymentId": 1, "reason": "Customer cancellation"}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments/refund', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    paymentNumber: "PAY-12345678",
    paymentId: 1,
    reason: "Customer cancellation"
  })
}).then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/payments/refund', {
  paymentNumber: "PAY-12345678",
  paymentId: 1,
  reason: "Customer cancellation"
}).then(response => console.log(response.data));
```

### Java Example
```java
RefundPaymentRequest req = new RefundPaymentRequest("PAY-12345678", 1L, "Customer cancellation");
HttpEntity<RefundPaymentRequest> entity = new HttpEntity<>(req, headers);
restTemplate.postForEntity("https://api.washgo.in/api/v1/payments/refund", entity, PaymentResponse.class);
```

### Notes
- Payment must be in `SUCCESS` state prior to refund.

---

## 4. Complete Razorpay Payment
- **API Name & Overview**: Complete Razorpay Payment - Webhook/Callback receiver to mark Razorpay payment complete.
- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/payments/razorpay/complete`
- **Full URL**: `https://api.washgo.in/api/v1/payments/razorpay/complete`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header Name | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "razorpayOrderId": "order_abc123",
  "razorpayPaymentId": "pay_xyz456",
  "razorpaySignature": "signature_hash_string",
  "paymentNumber": "PAY-12345678"
}
```
**Validation Rules**: None visible.

### Success Response (200 OK)
```json
{
  "id": 1,
  "paymentNumber": "PAY-12345678",
  "orderId": 101,
  "customerId": 201,
  "amount": 499.00,
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS",
  "gatewayTransactionId": null,
  "gatewayOrderId": "order_abc123",
  "gatewayPaymentId": "pay_xyz456",
  "remarks": "Payment for order #101",
  "createdAt": "2026-07-28T10:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Invalid signature.
- **404 Not Found**: Payment not found.
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment`
- **Operations**: Fetch by `paymentNumber`, update status and gateway IDs.

### Service Flow
`PaymentController` -> `PaymentServiceImpl.completeRazorpayPayment` -> Validate signature (optional here, might be done by service) -> Find payment -> Update fields -> Save.

### Events
- **Kafka Producer**: Publishes `PaymentSuccessEvent`.

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/payments/razorpay/complete \
  -H "Content-Type: application/json" \
  -d '{"razorpayOrderId": "order_abc123", "razorpayPaymentId": "pay_xyz456", "razorpaySignature": "hash", "paymentNumber": "PAY-12345678"}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments/razorpay/complete', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    razorpayOrderId: "order_abc123",
    razorpayPaymentId: "pay_xyz456",
    razorpaySignature: "hash",
    paymentNumber: "PAY-12345678"
  })
}).then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/payments/razorpay/complete', {
  razorpayOrderId: "order_abc123",
  razorpayPaymentId: "pay_xyz456",
  razorpaySignature: "hash",
  paymentNumber: "PAY-12345678"
}).then(response => console.log(response.data));
```

### Java Example
```java
VerifySignatureRequest req = new VerifySignatureRequest("order_abc123", "pay_xyz456", "hash", "PAY-12345678");
HttpEntity<VerifySignatureRequest> entity = new HttpEntity<>(req, headers);
restTemplate.postForEntity("https://api.washgo.in/api/v1/payments/razorpay/complete", entity, PaymentResponse.class);
```

### Notes
- Generally called securely by the frontend after Razorpay SDK successfully processes the transaction.

---

## 5. Get Payment by ID
- **API Name & Overview**: Get Payment by ID - Fetch a specific payment record via database primary key.
- **HTTP Method**: GET
- **Endpoint URL**: `/api/v1/payments/{id}`
- **Full URL**: `https://api.washgo.in/api/v1/payments/{id}`
- **Authentication**: Not Required
- **Roles**: None

### Headers
*None*

### Path Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | Long | Yes | Database ID of the payment |

### Query Parameters
*None*

### Request Body
*None*

### Success Response (200 OK)
```json
{
  "id": 1,
  "paymentNumber": "PAY-12345678",
  "orderId": 101,
  "customerId": 201,
  "amount": 499.00,
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS",
  "gatewayTransactionId": "txn_abc",
  "gatewayOrderId": "order_abc123",
  "gatewayPaymentId": "pay_xyz",
  "remarks": null,
  "createdAt": "2026-07-28T10:00:00Z"
}
```

### Error Responses
- **404 Not Found**: ResourceNotFoundException.
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment`
- **Operations**: `findById`

### Service Flow
`PaymentController` -> `PaymentServiceImpl.getPaymentById` -> `PaymentRepository.findById`.

### Events
- **Kafka**: None.

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/payments/1
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments/1')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/payments/1')
  .then(response => console.log(response.data));
```

### Java Example
```java
ResponseEntity<PaymentResponse> response = restTemplate.getForEntity("https://api.washgo.in/api/v1/payments/1", PaymentResponse.class);
```

### Notes
- None

---

## 6. Get Payment by Number
- **API Name & Overview**: Get Payment by Number - Fetch a payment by its uniquely generated paymentNumber.
- **HTTP Method**: GET
- **Endpoint URL**: `/api/v1/payments/number/{paymentNumber}`
- **Full URL**: `https://api.washgo.in/api/v1/payments/number/{paymentNumber}`
- **Authentication**: Not Required
- **Roles**: None

### Headers
*None*

### Path Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| `paymentNumber` | String | Yes | Payment number |

### Query Parameters
*None*

### Request Body
*None*

### Success Response (200 OK)
```json
{
  "id": 1,
  "paymentNumber": "PAY-12345678",
  "orderId": 101,
  "customerId": 201,
  "amount": 499.00,
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS",
  "gatewayTransactionId": "txn_abc",
  "gatewayOrderId": "order_abc123",
  "gatewayPaymentId": "pay_xyz",
  "remarks": null,
  "createdAt": "2026-07-28T10:00:00Z"
}
```

### Error Responses
- **404 Not Found**: ResourceNotFoundException.
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment`
- **Operations**: `findByPaymentNumber`

### Service Flow
`PaymentController` -> `PaymentServiceImpl.getPaymentByNumber` -> `PaymentRepository.findByPaymentNumber`.

### Events
- **Kafka**: None.

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/payments/number/PAY-12345678
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments/number/PAY-12345678')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/payments/number/PAY-12345678')
  .then(response => console.log(response.data));
```

### Java Example
```java
ResponseEntity<PaymentResponse> response = restTemplate.getForEntity("https://api.washgo.in/api/v1/payments/number/PAY-12345678", PaymentResponse.class);
```

### Notes
- None

---

## 7. Get Payments by Order
- **API Name & Overview**: Get Payments by Order - Get a list of payments associated with an orderId.
- **HTTP Method**: GET
- **Endpoint URL**: `/api/v1/payments/order/{orderId}`
- **Full URL**: `https://api.washgo.in/api/v1/payments/order/{orderId}`
- **Authentication**: Not Required
- **Roles**: None

### Headers
*None*

### Path Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| `orderId` | Long | Yes | Order ID |

### Query Parameters
*None*

### Request Body
*None*

### Success Response (200 OK)
```json
[
  {
    "id": 1,
    "paymentNumber": "PAY-12345678",
    "orderId": 101,
    "customerId": 201,
    "amount": 499.00,
    "paymentMethod": "UPI",
    "paymentStatus": "SUCCESS",
    "gatewayTransactionId": "txn_abc",
    "gatewayOrderId": "order_abc123",
    "gatewayPaymentId": "pay_xyz",
    "remarks": null,
    "createdAt": "2026-07-28T10:00:00Z"
  }
]
```

### Error Responses
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment`
- **Operations**: `findByOrderId`

### Service Flow
`PaymentController` -> `PaymentServiceImpl.getPaymentsByOrder` -> `PaymentRepository.findByOrderId`.

### Events
- **Kafka**: None.

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/payments/order/101
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments/order/101')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/payments/order/101')
  .then(response => console.log(response.data));
```

### Java Example
```java
ResponseEntity<PaymentResponse[]> response = restTemplate.getForEntity("https://api.washgo.in/api/v1/payments/order/101", PaymentResponse[].class);
```

### Notes
- Returns a list because multiple payment attempts may be tracked (e.g., failed attempts).

---

## 8. Get Payments by Customer
- **API Name & Overview**: Get Payments by Customer - Fetch all payments made by a given customer.
- **HTTP Method**: GET
- **Endpoint URL**: `/api/v1/payments/customer/{customerId}`
- **Full URL**: `https://api.washgo.in/api/v1/payments/customer/{customerId}`
- **Authentication**: Not Required
- **Roles**: None

### Headers
*None*

### Path Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| `customerId` | Long | Yes | Customer ID |

### Query Parameters
*None*

### Request Body
*None*

### Success Response (200 OK)
```json
[
  {
    "id": 1,
    "paymentNumber": "PAY-12345678",
    "orderId": 101,
    "customerId": 201,
    "amount": 499.00,
    "paymentMethod": "UPI",
    "paymentStatus": "SUCCESS",
    "gatewayTransactionId": "txn_abc",
    "gatewayOrderId": "order_abc123",
    "gatewayPaymentId": "pay_xyz",
    "remarks": null,
    "createdAt": "2026-07-28T10:00:00Z"
  }
]
```

### Error Responses
- **500 Internal Server Error**: Server errors.

### Database Tables
- **Entity**: `Payment`
- **Operations**: `findByCustomerId`

### Service Flow
`PaymentController` -> `PaymentServiceImpl.getPaymentsByCustomer` -> `PaymentRepository.findByCustomerId`.

### Events
- **Kafka**: None.

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/payments/customer/201
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/payments/customer/201')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/payments/customer/201')
  .then(response => console.log(response.data));
```

### Java Example
```java
ResponseEntity<PaymentResponse[]> response = restTemplate.getForEntity("https://api.washgo.in/api/v1/payments/customer/201", PaymentResponse[].class);
```

### Notes
- None

---

## 9. Create Razorpay Order
- **API Name & Overview**: Create Razorpay Order - Connects to Razorpay APIs to initialize a Razorpay order. Returns Razorpay keys and order ID to the client.
- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/razorpay/create-order`
- **Full URL**: `https://api.washgo.in/api/v1/razorpay/create-order`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header Name | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "orderId": 101,
  "customerId": 201,
  "amount": 49900
}
```
**Validation Rules**:
| Field | Type | Validation | Description |
|---|---|---|---|
| `orderId` | Long | `@NotNull` | Application order ID. |
| `customerId` | Long | `@NotNull` | Customer ID. |
| `amount` | Integer | `@NotNull` | Amount in paise (multiply INR by 100). |

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "razorpayOrderId": "order_K8abcdefgh1234",
    "keyId": "rzp_test_TFJKp9t9jnblPr",
    "amount": 49900,
    "currency": "INR"
  }
}
```

### Error Responses
- **400 Bad Request**: Validation errors.
- **500 Internal Server Error**: Gateway failures.

### Database Tables
- **Entity**: None directly touched by this endpoint (delegated to Razorpay).
- **Operations**: Uses Razorpay SDK `razorpayClient.orders.create`.

### Service Flow
`RazorpayController` -> `RazorpayServiceImpl.createOrder` -> Calls Razorpay API -> Wraps in `ApiResponse`.

### Events
- **Kafka**: None.

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/razorpay/create-order \
  -H "Content-Type: application/json" \
  -d '{"orderId": 101, "customerId": 201, "amount": 49900}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/razorpay/create-order', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    orderId: 101,
    customerId: 201,
    amount: 49900
  })
}).then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/razorpay/create-order', {
  orderId: 101,
  customerId: 201,
  amount: 49900
}).then(response => console.log(response.data));
```

### Java Example
```java
CreateRazorpayOrderRequest req = new CreateRazorpayOrderRequest(101L, 201L, 49900);
HttpEntity<CreateRazorpayOrderRequest> entity = new HttpEntity<>(req, headers);
ResponseEntity<ApiResponse> response = restTemplate.postForEntity("https://api.washgo.in/api/v1/razorpay/create-order", entity, ApiResponse.class);
```

### Notes
- Standard Razorpay SDK usage via `razorpay-java:1.4.8`.

---

## 10. Verify Razorpay Signature
- **API Name & Overview**: Verify Razorpay Signature - Validates the payload signature sent by Razorpay after successful client checkout.
- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/razorpay/verify`
- **Full URL**: `https://api.washgo.in/api/v1/razorpay/verify`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header Name | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "razorpayOrderId": "order_K8abcdefgh1234",
  "razorpayPaymentId": "pay_K8ijklmnop5678",
  "razorpaySignature": "abcd1234signaturehash",
  "paymentNumber": "PAY-12345678"
}
```
**Validation Rules**: None specified directly on fields.

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "paymentNumber": "PAY-12345678",
    "orderId": 101,
    "customerId": 201,
    "amount": 499.00,
    "paymentMethod": "UPI",
    "paymentStatus": "SUCCESS",
    "gatewayTransactionId": null,
    "gatewayOrderId": "order_K8abcdefgh1234",
    "gatewayPaymentId": "pay_K8ijklmnop5678",
    "remarks": null,
    "createdAt": "2026-07-28T10:00:00Z"
  }
}
```

### Error Responses
- **400 Bad Request**: Invalid signature.
- **500 Internal Server Error**: Verification failures.

### Database Tables
- **Entity**: None in RazorpayServiceImpl, but delegates internally to update Payment.
- **Operations**: Signature check using `Utils.verifyPaymentSignature`.

### Service Flow
`RazorpayController` -> `RazorpayServiceImpl.verifySignature` -> Signature Check -> Returns `ApiResponse<PaymentResponse>`.

### Events
- **Kafka**: Triggered via delegation if the payment is updated to SUCCESS.

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/razorpay/verify \
  -H "Content-Type: application/json" \
  -d '{"razorpayOrderId": "order_K8abcdefgh1234", "razorpayPaymentId": "pay_K8ijklmnop5678", "razorpaySignature": "hash", "paymentNumber": "PAY-12345678"}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/razorpay/verify', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    razorpayOrderId: "order_K8abcdefgh1234",
    razorpayPaymentId: "pay_K8ijklmnop5678",
    razorpaySignature: "hash",
    paymentNumber: "PAY-12345678"
  })
}).then(response => response.json())
  .then(data => console.log(data));
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/razorpay/verify', {
  razorpayOrderId: "order_K8abcdefgh1234",
  razorpayPaymentId: "pay_K8ijklmnop5678",
  razorpaySignature: "hash",
  paymentNumber: "PAY-12345678"
}).then(response => console.log(response.data));
```

### Java Example
```java
VerifySignatureRequest req = new VerifySignatureRequest("order_K8abcdefgh1234", "pay_K8ijklmnop5678", "hash", "PAY-12345678");
HttpEntity<VerifySignatureRequest> entity = new HttpEntity<>(req, headers);
ResponseEntity<ApiResponse> response = restTemplate.postForEntity("https://api.washgo.in/api/v1/razorpay/verify", entity, ApiResponse.class);
```

### Notes
- It validates the cryptographic signature from Razorpay.

---

## Endpoint Summary

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/payments` | Create a new payment record |
| POST | `/api/v1/payments/verify` | Verify a payment externally |
| POST | `/api/v1/payments/refund` | Refund a payment |
| POST | `/api/v1/payments/razorpay/complete` | Complete Razorpay transaction |
| GET | `/api/v1/payments/{id}` | Get a payment by Database ID |
| GET | `/api/v1/payments/number/{paymentNumber}` | Get a payment by Payment Number |
| GET | `/api/v1/payments/order/{orderId}` | Get all payments for an Order |
| GET | `/api/v1/payments/customer/{customerId}` | Get all payments for a Customer |
| POST | `/api/v1/razorpay/create-order` | Create a new Razorpay Order |
| POST | `/api/v1/razorpay/verify` | Verify Razorpay SDK signature |
