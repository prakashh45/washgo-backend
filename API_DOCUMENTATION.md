# WashGo Backend API Documentation

Last verified from source code: 2026-07-26

This document maps the APIs currently implemented in the WashGo backend. It is based on the checked-in Spring Boot source code, not an external Swagger export.

## 1. System Overview

WashGo is split into multiple Spring Boot services:

| Service | Main responsibility | Local profile port found in repo | Main API base paths |
| --- | --- | ---: | --- |
| API Gateway | Public entry point, Firebase auth, request forwarding | 8080 | Gateway routes |
| Auth Service | Firebase user sync and user identity persistence | 8082 in `application-local.properties` | `/api/v1/auth`, `/internal/users` |
| Catalog Service | Laundry partners and laundry services | 8081 in `application-local.properties` | `/api/catalog` |
| Order Service | Cart, order placement, order lookup, order status | 8083 in Docker Compose | `/api/v1/cart`, `/api/v1/orders` |
| Logistics Service | Delivery partners and delivery assignment workflow | 8084 | `/api/v1/delivery-partners`, `/api/v1/assignments` |
| Payment Service | Payment records, verification, refund, Razorpay integration | 8085 | `/api/v1/payments`, `/api/v1/razorpay` |
| Notification Service | Order placed notification endpoint | 8086 | `/api/v1/notifications` |
| Config Server | Externalized config | 8888 | Spring Cloud Config |
| Discovery Server | Eureka service discovery | 8761 | Eureka UI/API |

## 2. How To Call The APIs

Preferred public base URL:

```text
http://localhost:8080
```

Most APIs are intended to be called through the API Gateway with:

```http
Authorization: Bearer <firebase_id_token>
Content-Type: application/json
```

Gateway public endpoints:

| Endpoint | Auth required |
| --- | --- |
| `/api/v1/auth/**` | No |
| `/actuator/**` | No |
| `/api/v1/orders/health` | No |
| Everything else | Yes, Firebase bearer token required |

After validating Firebase, the gateway injects these internal headers before forwarding:

| Header | Meaning |
| --- | --- |
| `X-Gateway-Key` | Shared gateway secret |
| `X-User-Id` | Synced internal user UUID |
| `X-Firebase-Uid` | Firebase UID |
| `X-User-Role` | `CUSTOMER`, `PARTNER`, or `ADMIN` |

Do not expose or manually send `X-Gateway-Key` from frontend clients. It is intended for internal gateway-to-service calls.

## 3. Important Current Routing Issues

These are real mismatches found in the current source. Fix these before relying fully on gateway testing.

| Area | Current code | Impact |
| --- | --- | --- |
| Auth local port | Auth local config says `8082`, but gateway local route sends auth traffic to `8081` | `/api/v1/auth/**` may route to the wrong service locally |
| Catalog local port | Catalog local config says `8081`, but gateway local route sends catalog traffic to `8082` | Catalog may route to the wrong service locally |
| Catalog path | Controllers use `/api/catalog/**`, but gateway route expects `/api/v1/catalog/**` | Catalog APIs may not work through gateway until path or route is aligned |
| Gateway user sync | Gateway `AuthServiceClient` calls `/internal/auth/sync`, but auth controller exposes `/internal/users/sync` | Gateway auth flow may fail after Firebase validation |
| Logistics pickup | Order service calls `LOGISTICS-SERVICE /api/v1/logistics/pickup`, but that controller currently exists under order-service source, not logistics-service source | Order placement logistics callback can hit 404 or fallback |
| Logistics gateway route | Gateway only routes `/api/v1/logistics/**`, while real logistics APIs are `/api/v1/delivery-partners/**` and `/api/v1/assignments/**` | Delivery partner and assignment APIs are not reachable through gateway unless routes are added |
| Delivery partner status update | `PATCH /api/v1/delivery-partners/{id}/status` returns `null` from service | Endpoint exists but response/body behavior is broken |
| Payment event publishing | `PaymentEventProducer` exists, but `PaymentServiceImpl` does not call it | Verifying payment will not automatically publish `payment-success` to update order payment status |
| Order event fields | `OrderCreatedEvent.eventId` and `createdAt` are not set in `OrderServiceImpl` | Notification idempotency consumer expects `eventId` and may fail on null |
| Assignment OTP | OTPs are generated and stored, but not returned by assignment API responses | OTP verification needs another delivery channel, DB access, or a future notification flow |
| Notification endpoint | `POST /api/v1/notifications/order-placed` currently logs and returns a string | It does not persist/send through `NotificationServiceImpl` |

## 4. Response Conventions

Not every service uses the same response envelope.

Plain object/list response examples:

```json
{
  "id": 1,
  "orderNumber": "ORD-20260726-0001"
}
```

Logistics and Razorpay wrapped response:

```json
{
  "success": true,
  "message": "Assignment fetched successfully.",
  "data": {},
  "timestamp": "2026-07-26T10:15:30"
}
```

Order service error response:

```json
{
  "timestamp": "2026-07-26T10:15:30",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Cart not found",
  "path": "/api/v1/cart/1"
}
```

## 5. Endpoint Index

| Method | Path | Service | Purpose |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/sync` | Auth | Verify Firebase token and create/get user |
| `GET` | `/api/v1/auth/health` | Auth | Health check |
| `POST` | `/internal/users/sync` | Auth | Internal user sync |
| `POST` | `/api/catalog/partners` | Catalog | Create laundry partner |
| `GET` | `/api/catalog/partners` | Catalog | List laundry partners |
| `GET` | `/api/catalog/partners/{id}` | Catalog | Get laundry partner |
| `POST` | `/api/catalog/partners/{partnerId}/services` | Catalog | Add service for partner |
| `GET` | `/api/catalog/partners/{partnerId}/services` | Catalog | List services for partner |
| `POST` | `/api/v1/cart` | Order | Create cart |
| `GET` | `/api/v1/cart/{customerId}` | Order | Get cart by customer |
| `POST` | `/api/v1/cart/{customerId}/items` | Order | Add item to cart |
| `DELETE` | `/api/v1/cart/items/{cartItemId}` | Order | Remove cart item |
| `DELETE` | `/api/v1/cart/{customerId}` | Order | Clear cart |
| `POST` | `/api/v1/orders` | Order | Place order |
| `GET` | `/api/v1/orders/{orderId}` | Order | Get order by ID |
| `GET` | `/api/v1/orders/number/{orderNumber}` | Order | Get order by order number |
| `GET` | `/api/v1/orders/customer/{customerId}` | Order | List customer orders |
| `GET` | `/api/v1/orders/partner/{partnerId}` | Order | List partner orders |
| `PATCH` | `/api/v1/orders/{orderId}/status` | Order | Update order status |
| `DELETE` | `/api/v1/orders/{orderId}` | Order | Cancel order |
| `GET` | `/api/v1/orders/health` | Order | Health check |
| `POST` | `/api/v1/logistics/pickup` | Order source stub | Assign pickup partner stub |
| `POST` | `/api/v1/delivery-partners` | Logistics | Create delivery partner |
| `GET` | `/api/v1/delivery-partners` | Logistics | List delivery partners |
| `GET` | `/api/v1/delivery-partners/{id}` | Logistics | Get delivery partner |
| `PUT` | `/api/v1/delivery-partners/{id}` | Logistics | Update delivery partner |
| `PATCH` | `/api/v1/delivery-partners/{id}/availability` | Logistics | Update availability |
| `PATCH` | `/api/v1/delivery-partners/{id}/location` | Logistics | Update location |
| `PATCH` | `/api/v1/delivery-partners/{id}/status` | Logistics | Update status, currently broken |
| `POST` | `/api/v1/assignments` | Logistics | Assign delivery partner |
| `GET` | `/api/v1/assignments/{id}` | Logistics | Get assignment |
| `GET` | `/api/v1/assignments/order/{orderId}` | Logistics | List assignments by order |
| `GET` | `/api/v1/assignments/partner/{partnerId}` | Logistics | List assignments by partner |
| `PUT` | `/api/v1/assignments/{id}/accept` | Logistics | Accept assignment |
| `PUT` | `/api/v1/assignments/{id}/reject` | Logistics | Reject assignment |
| `PUT` | `/api/v1/assignments/{id}/start` | Logistics | Start trip |
| `PUT` | `/api/v1/assignments/{id}/arrived` | Logistics | Mark arrived |
| `PUT` | `/api/v1/assignments/{id}/verify-otp` | Logistics | Verify pickup/delivery OTP |
| `PUT` | `/api/v1/assignments/{id}/complete` | Logistics | Complete assignment |
| `POST` | `/api/v1/payments` | Payment | Create payment |
| `POST` | `/api/v1/payments/verify` | Payment | Verify generic payment |
| `POST` | `/api/v1/payments/refund` | Payment | Refund payment |
| `POST` | `/api/v1/payments/razorpay/complete` | Payment | Complete Razorpay payment |
| `GET` | `/api/v1/payments/{id}` | Payment | Get payment by ID |
| `GET` | `/api/v1/payments/number/{paymentNumber}` | Payment | Get payment by number |
| `GET` | `/api/v1/payments/order/{orderId}` | Payment | List payments by order |
| `GET` | `/api/v1/payments/customer/{customerId}` | Payment | List payments by customer |
| `POST` | `/api/v1/razorpay/create-order` | Payment | Create Razorpay order |
| `POST` | `/api/v1/razorpay/verify` | Payment | Verify Razorpay signature |
| `POST` | `/api/v1/notifications/order-placed` | Notification | Order placed notification stub |

## 6. Auth Service

### POST `/api/v1/auth/sync`

Public endpoint. Requires `Authorization: Bearer <firebase_id_token>`.

Verifies the Firebase ID token, creates the user if missing, and returns the user profile.

Request body: none.

Response:

```json
{
  "id": "a1111111-1111-1111-1111-111111111111",
  "firebaseUid": "firebase_uid",
  "email": "customer@example.com",
  "fullName": "Customer Name",
  "phoneNumber": null,
  "profileImage": "https://example.com/photo.png",
  "role": "CUSTOMER"
}
```

New users default to:

| Field | Default |
| --- | --- |
| `role` | `CUSTOMER` |
| `active` | `true` |
| `email` | Empty string if Firebase email is null |
| `fullName` | `Unknown User` if Firebase name is null |

### GET `/api/v1/auth/health`

Returns:

```text
Auth Service Running
```

### POST `/internal/users/sync`

Internal endpoint used to sync Firebase identity to database.

Request:

```json
{
  "firebaseUid": "firebase_uid",
  "email": "customer@example.com",
  "fullName": "Customer Name",
  "phoneNumber": "9999999999",
  "profileImage": "https://example.com/photo.png"
}
```

Response:

```json
{
  "userId": "a1111111-1111-1111-1111-111111111111",
  "firebaseUid": "firebase_uid",
  "role": "CUSTOMER",
  "active": true
}
```

## 7. Catalog Service

Current controller base path is `/api/catalog`, not `/api/v1/catalog`.

### POST `/api/catalog/partners`

Creates a laundry partner.

Request:

```json
{
  "shopName": "Sparkle Laundry",
  "ownerName": "Ravi Kumar",
  "phoneNumber": "9876543210",
  "email": "sparkle@example.com",
  "address": "12 Main Road",
  "city": "Hyderabad",
  "latitude": 17.385,
  "longitude": 78.4867,
  "coverImage": "https://example.com/shop.jpg"
}
```

Response `201 Created`:

```json
{
  "id": 1,
  "shopName": "Sparkle Laundry",
  "ownerName": "Ravi Kumar",
  "phoneNumber": "9876543210",
  "email": "sparkle@example.com",
  "address": "12 Main Road",
  "city": "Hyderabad",
  "latitude": 17.385,
  "longitude": 78.4867,
  "coverImage": "https://example.com/shop.jpg",
  "verified": false,
  "holidayMode": false,
  "averageRating": 0.0,
  "totalReviews": 0,
  "active": true
}
```

### GET `/api/catalog/partners`

Returns `LaundryPartnerResponse[]`.

### GET `/api/catalog/partners/{id}`

Returns one `LaundryPartnerResponse`.

### POST `/api/catalog/partners/{partnerId}/services`

Creates a service offered by a laundry partner.

Request:

```json
{
  "serviceName": "Wash and Fold",
  "description": "Regular clothes wash, dry, and fold"
}
```

Response `201 Created`:

```json
{
  "id": 1,
  "serviceName": "Wash and Fold",
  "description": "Regular clothes wash, dry, and fold",
  "active": true
}
```

### GET `/api/catalog/partners/{partnerId}/services`

Returns `LaundryServiceResponse[]`.

## 8. Cart APIs

Base path: `/api/v1/cart`

### POST `/api/v1/cart`

Creates an empty cart for a customer and laundry partner.

Request:

```json
{
  "customerId": 1,
  "laundryPartnerId": 10
}
```

Validation:

| Field | Rule |
| --- | --- |
| `customerId` | required |
| `laundryPartnerId` | required |

Response `201 Created`:

```json
{
  "id": 1,
  "customerId": 1,
  "laundryPartnerId": 10,
  "totalItems": 0,
  "totalAmount": 0,
  "items": []
}
```

### GET `/api/v1/cart/{customerId}`

Returns cart by customer ID.

### POST `/api/v1/cart/{customerId}/items`

Adds an item to the customer's cart.

Request:

```json
{
  "serviceId": 101,
  "serviceName": "Wash and Fold",
  "quantity": 2,
  "unitPrice": 99.00
}
```

Validation:

| Field | Rule |
| --- | --- |
| `serviceId` | required |
| `serviceName` | required, non-blank |
| `quantity` | required, minimum `1` |
| `unitPrice` | required |

Behavior:

- `totalPrice` is calculated as `unitPrice * quantity`.
- Cart `totalAmount` is recalculated from all cart items.
- Cart `totalItems` is set to number of cart item rows, not sum of quantities.

Response:

```json
{
  "id": 1,
  "customerId": 1,
  "laundryPartnerId": 10,
  "totalItems": 1,
  "totalAmount": 198.00,
  "items": [
    {
      "id": 1,
      "serviceId": 101,
      "serviceName": "Wash and Fold",
      "quantity": 2,
      "unitPrice": 99.00,
      "totalPrice": 198.00
    }
  ]
}
```

### DELETE `/api/v1/cart/items/{cartItemId}`

Deletes one cart item.

Response: `204 No Content`

### DELETE `/api/v1/cart/{customerId}`

Clears all items from the customer cart and resets totals.

Response: `204 No Content`

## 9. Order APIs

Base path: `/api/v1/orders`

### POST `/api/v1/orders`

Places an order.

Request:

```json
{
  "customerId": 1,
  "laundryPartnerId": 10,
  "pickupAddressId": 501,
  "paymentMethod": "UPI",
  "items": [
    {
      "serviceId": 101,
      "serviceName": "Wash and Fold",
      "quantity": 2,
      "unitPrice": 99.00
    }
  ]
}
```

Validation:

| Field | Rule |
| --- | --- |
| `customerId` | required |
| `laundryPartnerId` | required |
| `pickupAddressId` | required |
| `paymentMethod` | required |
| `items` | required |
| `items[].serviceId` | required |
| `items[].serviceName` | required, non-blank |
| `items[].quantity` | required, minimum `1` |
| `items[].unitPrice` | required |

Behavior:

- Generates `orderNumber`.
- Sets `orderStatus` to `PLACED`.
- Sets `paymentStatus` to `PENDING`.
- Calculates `totalAmount` from item totals.
- Publishes an `OrderCreatedEvent` to Kafka topic `order-created`.
- Calls logistics pickup assignment through Feign.
- Calls notification service `/api/v1/notifications/order-placed`.

Response `201 Created`:

```json
{
  "id": 1,
  "orderNumber": "ORD-20260726-0001",
  "customerId": 1,
  "laundryPartnerId": 10,
  "orderStatus": "PLACED",
  "paymentStatus": "PENDING",
  "totalAmount": 198.00,
  "items": [
    {
      "serviceId": 101,
      "serviceName": "Wash and Fold",
      "quantity": 2,
      "unitPrice": 99.00,
      "totalPrice": 198.00
    }
  ]
}
```

### GET `/api/v1/orders/{orderId}`

Returns one order by database ID.

### GET `/api/v1/orders/number/{orderNumber}`

Returns one order by order number.

### GET `/api/v1/orders/customer/{customerId}`

Returns `OrderResponse[]` for one customer.

### GET `/api/v1/orders/partner/{partnerId}`

Returns `OrderResponse[]` for one laundry partner.

### PATCH `/api/v1/orders/{orderId}/status`

Updates the order status.

Request:

```json
{
  "orderStatus": "WASHING"
}
```

Response: updated `OrderResponse`.

### DELETE `/api/v1/orders/{orderId}`

Cancels the order by setting `orderStatus` to `CANCELLED`.

Response: `204 No Content`

### GET `/api/v1/orders/health`

Public health endpoint.

Returns:

```text
Order Service is UP
```

## 10. Order Service Logistics Stub

### POST `/api/v1/logistics/pickup`

This controller currently exists under order-service source. It logs pickup assignment data and returns a string.

Request:

```json
{
  "orderId": 1,
  "deliveryPartnerId": 10,
  "legType": "PICKUP"
}
```

Response:

```text
Pickup Assigned Successfully
```

Important: the order service Feign client expects this endpoint to exist in `LOGISTICS-SERVICE`, but the controller is currently in order-service source.

## 11. Delivery Partner APIs

Base path: `/api/v1/delivery-partners`

### POST `/api/v1/delivery-partners`

Creates a delivery partner profile.

Request:

```json
{
  "userId": 1,
  "vehicleType": "BIKE",
  "vehicleNumber": "TS09AB1234",
  "drivingLicense": "DL123456789",
  "aadhaarNumber": "123456789012"
}
```

Validation:

| Field | Rule |
| --- | --- |
| `userId` | required |
| `vehicleType` | required, non-blank |
| `vehicleNumber` | required, non-blank |
| `drivingLicense` | required, non-blank |
| `aadhaarNumber` | required, non-blank |

Behavior:

- Rejects duplicate `userId`.
- Rejects duplicate `vehicleNumber`.
- Defaults `rating` to `0.0`.
- Defaults `totalDeliveries` to `0`.
- Defaults `available` to `true`.
- Defaults `verified` to `false`.
- Defaults `status` to `ACTIVE`.

Response `201 Created`:

```json
{
  "id": 1,
  "userId": 1,
  "vehicleType": "BIKE",
  "vehicleNumber": "TS09AB1234",
  "drivingLicense": "DL123456789",
  "aadhaarNumber": "123456789012",
  "currentLatitude": null,
  "currentLongitude": null,
  "rating": 0.0,
  "totalDeliveries": 0,
  "available": true,
  "verified": false,
  "status": "ACTIVE"
}
```

### GET `/api/v1/delivery-partners`

Returns `DeliveryPartnerResponse[]`.

### GET `/api/v1/delivery-partners/{id}`

Returns one `DeliveryPartnerResponse`.

### PUT `/api/v1/delivery-partners/{id}`

Updates vehicle and document details.

Request:

```json
{
  "vehicleType": "BIKE",
  "vehicleNumber": "TS09AB1234",
  "drivingLicense": "DL123456789",
  "aadhaarNumber": "123456789012"
}
```

Response: updated `DeliveryPartnerResponse`.

### PATCH `/api/v1/delivery-partners/{id}/availability`

Request:

```json
{
  "available": false
}
```

Response: updated `DeliveryPartnerResponse`.

### PATCH `/api/v1/delivery-partners/{id}/location`

Request:

```json
{
  "currentLatitude": 17.385,
  "currentLongitude": 78.4867
}
```

Response: updated `DeliveryPartnerResponse`.

### PATCH `/api/v1/delivery-partners/{id}/status`

Request:

```json
{
  "status": "BUSY"
}
```

Current behavior: endpoint exists, but `DeliveryPartnerServiceImpl.updateStatus` returns `null`. Implement before using.

## 12. Delivery Assignment APIs

Base path: `/api/v1/assignments`

All successful responses are wrapped:

```json
{
  "success": true,
  "message": "Assignment fetched successfully.",
  "data": {},
  "timestamp": "2026-07-26T10:15:30"
}
```

### POST `/api/v1/assignments`

Assigns a delivery partner to an order leg.

Request:

```json
{
  "orderId": 1,
  "deliveryPartnerId": 1,
  "legType": "PICKUP"
}
```

Validation:

| Field | Rule |
| --- | --- |
| `orderId` | required |
| `deliveryPartnerId` | required |
| `legType` | required |

Behavior:

- Rejects if assignment already exists for same `orderId` and `legType`.
- Rejects if delivery partner is not `ACTIVE`.
- Creates assignment with status `ASSIGNED`.
- Generates pickup and delivery OTP values internally.
- Sets delivery partner status to `BUSY`.

Response `201 Created`:

```json
{
  "success": true,
  "message": "Delivery partner assigned successfully.",
  "data": {
    "id": 1,
    "orderId": 1,
    "deliveryPartnerId": 1,
    "deliveryPartnerName": null,
    "legType": "PICKUP",
    "status": "ASSIGNED",
    "assignedAt": "2026-07-26T10:15:30",
    "acceptedAt": null,
    "completedAt": null
  },
  "timestamp": "2026-07-26T10:15:30"
}
```

### GET `/api/v1/assignments/{id}`

Returns one assignment.

### GET `/api/v1/assignments/order/{orderId}`

Returns assignments for an order.

### GET `/api/v1/assignments/partner/{partnerId}`

Returns assignments for a delivery partner.

### PUT `/api/v1/assignments/{id}/accept`

Allowed only when current status is `ASSIGNED`.

Sets:

| Field | Value |
| --- | --- |
| `status` | `ACCEPTED` |
| `acceptedAt` | current time |

### PUT `/api/v1/assignments/{id}/reject`

Allowed only when current status is `ASSIGNED`.

Sets:

| Field | Value |
| --- | --- |
| assignment `status` | `REJECTED` |
| partner `status` | `ACTIVE` |

### PUT `/api/v1/assignments/{id}/start`

Allowed only when current status is `ACCEPTED`.

Sets assignment status to `EN_ROUTE`.

### PUT `/api/v1/assignments/{id}/arrived`

Allowed only when current status is `EN_ROUTE`.

Sets assignment status to `ARRIVED`.

### PUT `/api/v1/assignments/{id}/verify-otp`

Allowed only when current status is `ARRIVED`.

Request:

```json
{
  "assignmentId": 1,
  "otp": "1234"
}
```

Behavior:

- For `PICKUP` leg, checks `pickupOtp`.
- For `DELIVERY` leg, checks `deliveryOtp`.
- Sets status to `OTP_VERIFIED` when valid.

Note: path `{id}` is used by controller as the assignment ID. Body `assignmentId` is validated by DTO but not used by service call.

### PUT `/api/v1/assignments/{id}/complete`

Allowed only when current status is `OTP_VERIFIED`.

Sets:

| Field | Value |
| --- | --- |
| assignment `status` | `COMPLETED` |
| assignment `completedAt` | current time |
| partner `status` | `ACTIVE` |

## 13. Payment APIs

Base path: `/api/v1/payments`

### POST `/api/v1/payments`

Creates a payment record.

Request:

```json
{
  "orderId": 1,
  "customerId": 1,
  "amount": 198.00,
  "paymentMethod": "UPI",
  "remarks": "Initial payment"
}
```

Validation:

| Field | Rule |
| --- | --- |
| `orderId` | required |
| `customerId` | required |
| `amount` | required |
| `paymentMethod` | required |
| `remarks` | optional |

Behavior:

- Rejects if payment already exists for `orderId`.
- Generates `paymentNumber` like `PAY-YYYYMMDD-0001`.
- Sets `paymentStatus` to `PENDING`.

Response `201 Created`:

```json
{
  "id": 1,
  "paymentNumber": "PAY-20260726-0001",
  "orderId": 1,
  "customerId": 1,
  "amount": 198.00,
  "paymentMethod": "UPI",
  "paymentStatus": "PENDING",
  "gatewayTransactionId": null,
  "gatewayOrderId": null,
  "gatewayPaymentId": null,
  "remarks": "Initial payment",
  "createdAt": "2026-07-26T10:15:30"
}
```

### POST `/api/v1/payments/verify`

Marks a generic payment as successful.

Request:

```json
{
  "paymentNumber": "PAY-20260726-0001",
  "gatewayPaymentId": "pay_123",
  "gatewayTransactionId": "txn_123"
}
```

Behavior:

- Rejects missing payment.
- Rejects already successful payment.
- Sets `gatewayPaymentId`.
- Sets `gatewayTransactionId`.
- Sets `paymentStatus` to `SUCCESS`.

Response: updated `PaymentResponse`.

### POST `/api/v1/payments/refund`

Refunds a successful payment.

Request:

```json
{
  "paymentNumber": "PAY-20260726-0001",
  "paymentId": 1,
  "reason": "Customer cancellation"
}
```

Behavior:

- Looks up payment by `paymentNumber`.
- Only payments with status `SUCCESS` can be refunded.
- Sets `paymentStatus` to `REFUNDED`.
- Stores `reason` in `remarks`.
- `paymentId` is required by DTO but is not used by service lookup.

Response: updated `PaymentResponse`.

### POST `/api/v1/payments/razorpay/complete`

Completes Razorpay payment from payment controller.

Request:

```json
{
  "razorpayOrderId": "order_123",
  "razorpayPaymentId": "pay_123",
  "razorpaySignature": "signature",
  "paymentNumber": "PAY-20260726-0001"
}
```

Behavior:

- Does not verify Razorpay signature in this controller method.
- Calls `completeRazorpayPayment`.
- Sets `gatewayOrderId` to `razorpayOrderId`.
- Sets `gatewayPaymentId` and `gatewayTransactionId` to `razorpayPaymentId`.
- Sets `paymentStatus` to `SUCCESS`.

Response: updated `PaymentResponse`.

### GET `/api/v1/payments/{id}`

Returns payment by database ID.

### GET `/api/v1/payments/number/{paymentNumber}`

Returns payment by generated payment number.

### GET `/api/v1/payments/order/{orderId}`

Returns payments for one order.

### GET `/api/v1/payments/customer/{customerId}`

Returns payments for one customer.

## 14. Razorpay APIs

Base path: `/api/v1/razorpay`

### POST `/api/v1/razorpay/create-order`

Creates a Razorpay order.

Request:

```json
{
  "orderId": 1,
  "customerId": 1,
  "amount": 19800
}
```

Important: `amount` is in paise.

Response:

```json
{
  "success": true,
  "message": "Razorpay order created successfully",
  "data": {
    "razorpayOrderId": "order_123",
    "keyId": "rzp_test_xxx",
    "amount": 19800,
    "currency": "INR"
  },
  "timestamp": "2026-07-26T10:15:30"
}
```

### POST `/api/v1/razorpay/verify`

Verifies Razorpay signature and completes payment.

Request:

```json
{
  "razorpayOrderId": "order_123",
  "razorpayPaymentId": "pay_123",
  "razorpaySignature": "signature",
  "paymentNumber": "PAY-20260726-0001"
}
```

Behavior:

- Verifies signature using Razorpay SDK.
- Throws runtime error on invalid signature.
- On success, completes payment using `paymentNumber`.

Response:

```json
{
  "success": true,
  "message": "Payment verified successfully",
  "data": {
    "id": 1,
    "paymentNumber": "PAY-20260726-0001",
    "paymentStatus": "SUCCESS"
  },
  "timestamp": "2026-07-26T10:15:30"
}
```

## 15. Notification API

Base path: `/api/v1/notifications`

### POST `/api/v1/notifications/order-placed`

Current controller logs an order placed notification and returns a success string.

Request:

```json
{
  "userId": "1",
  "recipient": "customer@example.com",
  "title": "Order Placed",
  "message": "Your order has been placed successfully.",
  "type": "EMAIL"
}
```

Response:

```text
Notification Sent Successfully
```

Important: `NotificationServiceImpl` contains methods for saving/sending notifications, but `NotificationController` does not currently call it.

## 16. Enum Reference

### Role

```text
CUSTOMER
PARTNER
ADMIN
```

### PaymentMethod

```text
CASH_ON_DELIVERY
UPI
CARD
NET_BANKING
WALLET
```

### OrderStatus

```text
PLACED
PICKUP_ASSIGNED
PICKUP_ACCEPTED
PICKED_UP
RECEIVED_AT_LAUNDRY
WASHING
DRYING
IRONING
QUALITY_CHECK
READY_FOR_DELIVERY
DELIVERY_ASSIGNED
OUT_FOR_DELIVERY
DELIVERED
COMPLETED
CANCELLED
```

### Order Service PaymentStatus

```text
PENDING
SUCCESS
FAILED
REFUNDED
```

### Payment Service PaymentStatus

```text
PENDING
SUCCESS
FAILED
REFUNDED
CANCELLED
```

### DeliveryPartnerStatus

```text
ACTIVE
INACTIVE
BUSY
SUSPENDED
```

### DeliveryLegType

```text
PICKUP
DELIVERY
```

### AssignmentStatus

```text
ASSIGNED
ACCEPTED
REJECTED
EN_ROUTE
ARRIVED
OTP_VERIFIED
COMPLETED
CANCELLED
```

### NotificationType

```text
EMAIL
SMS
PUSH
IN_APP
```

### NotificationStatus

```text
PENDING
SENT
FAILED
```

## 17. Main Happy-Path Test Flow

Use this order when testing with Postman or frontend integration:

1. Get Firebase ID token from client app.
2. Call `POST /api/v1/auth/sync` with bearer token.
3. Create laundry partner using `POST /api/catalog/partners`.
4. Create laundry services using `POST /api/catalog/partners/{partnerId}/services`.
5. Create cart using `POST /api/v1/cart`.
6. Add cart items using `POST /api/v1/cart/{customerId}/items`.
7. Place order using `POST /api/v1/orders`.
8. Create payment using `POST /api/v1/payments`.
9. For Razorpay, call `POST /api/v1/razorpay/create-order`.
10. Verify Razorpay using `POST /api/v1/razorpay/verify`.
11. Assign delivery partner using `POST /api/v1/assignments`.
12. Move assignment through accept, start, arrived, verify OTP, complete.

## 18. Security And Configuration Notes

- Secrets are currently present in local property files. Move database passwords, mail passwords, gateway secret, Firebase credentials, and Razorpay keys to environment variables or a secure secret manager before production.
- Frontend clients should only call the API Gateway.
- Internal services should reject direct public calls unless intentionally exposed.
- Align gateway routes with controller paths before frontend integration.
- Add OpenAPI/Swagger documentation once routes are stable.

## 19. Suggested Fix Priority

1. Fix gateway auth sync URL from `/internal/auth/sync` to `/internal/users/sync`.
2. Align Auth and Catalog local ports or gateway routes.
3. Decide whether Catalog path should be `/api/catalog` or `/api/v1/catalog`, then make gateway and controllers match.
4. Move `/api/v1/logistics/pickup` controller into logistics-service or change Feign client target.
5. Add gateway routes for `/api/v1/delivery-partners/**` and `/api/v1/assignments/**`.
6. Implement `DeliveryPartnerServiceImpl.updateStatus`.
7. Wire `PaymentEventProducer` into successful payment verification.
8. Set `eventId` and `createdAt` when publishing `OrderCreatedEvent`.
9. Connect `NotificationController` to `NotificationServiceImpl`.
10. Move all secrets out of checked-in property files.

