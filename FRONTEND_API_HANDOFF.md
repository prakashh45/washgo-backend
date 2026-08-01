# WashGo Frontend API Handoff

Base URL:

```text
http://16.171.148.177:8080
```

Use the API gateway base URL for frontend calls. Do not call internal service URLs from the frontend.

## Auth

Most APIs require a Firebase ID token:

```http
Authorization: Bearer <firebase-id-token>
Content-Type: application/json
```

Gateway-public routes:

- `GET /actuator/health`
- `GET /api/v1/orders/health`
- `POST /api/v1/auth/sync` still needs the Firebase bearer token because the auth service verifies it.

## Auth API

### Sync Logged-In Firebase User

```http
POST /api/v1/auth/sync
Authorization: Bearer <firebase-id-token>
```

## Catalog APIs

### Create Laundry Partner

```http
POST /api/catalog/partners
```

```json
{
  "shopName": "Fresh Wash",
  "ownerName": "Prakash",
  "phoneNumber": "9876543210",
  "email": "partner@example.com",
  "address": "MG Road",
  "city": "Bengaluru",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "coverImage": "https://example.com/shop.jpg"
}
```

### Get Laundry Partners

```http
GET /api/catalog/partners
GET /api/catalog/partners/{id}
```

### Create Partner Service

```http
POST /api/catalog/partners/{partnerId}/services
```

```json
{
  "serviceName": "Wash and Fold",
  "description": "Daily laundry wash and fold"
}
```

### Get Partner Services

```http
GET /api/catalog/partners/{partnerId}/services
```

## Cart APIs

### Create Cart

```http
POST /api/v1/cart
```

```json
{
  "customerId": 1,
  "laundryPartnerId": 1
}
```

### Get Cart

```http
GET /api/v1/cart/{customerId}
```

### Add Cart Item

```http
POST /api/v1/cart/{customerId}/items
```

```json
{
  "serviceId": 1,
  "serviceName": "Wash and Fold",
  "quantity": 2,
  "unitPrice": 120
}
```

### Remove or Clear Cart

```http
DELETE /api/v1/cart/items/{cartItemId}
DELETE /api/v1/cart/{customerId}
```

## Order APIs

### Place Order

```http
POST /api/v1/orders
```

```json
{
  "customerId": 1,
  "laundryPartnerId": 1,
  "pickupAddressId": 1,
  "paymentMethod": "CASH_ON_DELIVERY",
  "items": [
    {
      "serviceId": 1,
      "serviceName": "Wash and Fold",
      "quantity": 2,
      "unitPrice": 120
    }
  ]
}
```

### Get Orders

```http
GET /api/v1/orders/{orderId}
GET /api/v1/orders/number/{orderNumber}
GET /api/v1/orders/customer/{customerId}
GET /api/v1/orders/partner/{partnerId}
```

### Update or Cancel Order

```http
PATCH /api/v1/orders/{orderId}/status
DELETE /api/v1/orders/{orderId}
```

```json
{
  "orderStatus": "PLACED"
}
```

Allowed order statuses:

```text
PLACED, PICKUP_ASSIGNED, PICKUP_ACCEPTED, PICKED_UP, RECEIVED_AT_LAUNDRY,
WASHING, DRYING, IRONING, QUALITY_CHECK, READY_FOR_DELIVERY,
DELIVERY_ASSIGNED, OUT_FOR_DELIVERY, DELIVERED, COMPLETED, CANCELLED
```

## Payment APIs

### Create Payment

```http
POST /api/v1/payments
```

```json
{
  "orderId": 1,
  "customerId": 1,
  "amount": 240,
  "paymentMethod": "UPI",
  "remarks": "Frontend payment created"
}
```

Payment methods:

```text
CASH_ON_DELIVERY, UPI, CARD, NET_BANKING, WALLET
```

### Verify Payment

```http
POST /api/v1/payments/verify
```

```json
{
  "paymentNumber": "PAY-000001",
  "gatewayPaymentId": "pay_xxx",
  "gatewayTransactionId": "txn_xxx"
}
```

### Refund Payment

```http
POST /api/v1/payments/refund
```

```json
{
  "paymentNumber": "PAY-000001",
  "paymentId": 1,
  "reason": "Customer cancelled"
}
```

### Get Payments

```http
GET /api/v1/payments/{id}
GET /api/v1/payments/number/{paymentNumber}
GET /api/v1/payments/order/{orderId}
GET /api/v1/payments/customer/{customerId}
```

## Razorpay APIs

### Create Razorpay Order

```http
POST /api/v1/razorpay/create-order
```

```json
{
  "orderId": 1,
  "customerId": 1,
  "amount": 24000
}
```

`amount` is in paise.

### Verify Razorpay Payment

```http
POST /api/v1/razorpay/verify
POST /api/v1/payments/razorpay/complete
```

```json
{
  "razorpayOrderId": "order_xxx",
  "razorpayPaymentId": "pay_xxx",
  "razorpaySignature": "signature_xxx",
  "paymentNumber": "PAY-000001"
}
```

## Delivery Partner APIs

### Create Delivery Partner

```http
POST /api/v1/delivery-partners
```

```json
{
  "userId": 2,
  "vehicleType": "BIKE",
  "vehicleNumber": "KA01AB1234",
  "drivingLicense": "DL1234567890",
  "aadhaarNumber": "123456789012"
}
```

### Get or Update Delivery Partners

```http
GET /api/v1/delivery-partners
GET /api/v1/delivery-partners/{id}
PUT /api/v1/delivery-partners/{id}
PATCH /api/v1/delivery-partners/{id}/availability
PATCH /api/v1/delivery-partners/{id}/location
PATCH /api/v1/delivery-partners/{id}/status
```

```json
{
  "available": true
}
```

```json
{
  "currentLatitude": 12.9716,
  "currentLongitude": 77.5946
}
```

```json
{
  "status": "ACTIVE"
}
```

Delivery partner statuses:

```text
ACTIVE, INACTIVE, BUSY, SUSPENDED
```

## Assignment and Logistics APIs

### Assign Delivery

```http
POST /api/v1/assignments
POST /api/v1/logistics/pickup
```

```json
{
  "orderId": 1,
  "deliveryPartnerId": 1,
  "legType": "PICKUP"
}
```

Delivery leg types:

```text
PICKUP, DELIVERY
```

### Get Assignments

```http
GET /api/v1/assignments/{id}
GET /api/v1/assignments/order/{orderId}
GET /api/v1/assignments/partner/{partnerId}
```

### Assignment Actions

```http
PUT /api/v1/assignments/{id}/accept
PUT /api/v1/assignments/{id}/reject
PUT /api/v1/assignments/{id}/start
PUT /api/v1/assignments/{id}/arrived
PUT /api/v1/assignments/{id}/verify-otp
PUT /api/v1/assignments/{id}/complete
```

```json
{
  "otp": "123456"
}
```

## Notification APIs

### Create Notification

```http
POST /api/v1/notifications/order-placed
```

```json
{
  "userId": "1",
  "recipient": "user@example.com",
  "title": "Order placed",
  "message": "Your order was placed successfully",
  "type": "IN_APP"
}
```

Notification types:

```text
EMAIL, SMS, PUSH, IN_APP
```

### Get Notifications

```http
GET /api/v1/notifications
GET /api/v1/notifications/{id}
GET /api/v1/notifications/user/{userId}
```

## Quick Smoke Test

```bash
curl http://16.171.148.177:8080/actuator/health
```

After login in the frontend, use the Firebase ID token:

```bash
curl http://16.171.148.177:8080/api/catalog/partners \
  -H "Authorization: Bearer <firebase-id-token>"
```
