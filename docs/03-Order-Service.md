# Order Service API Documentation

## Service Overview
- **Name**: order-service
- **Port**: Configured via Config Server
- **Base URLs**: `/api/v1/orders`, `/api/v1/cart`, `/api/v1/logistics`
- **Package**: `com.washgo`
- **Database**: PostgreSQL
- **Key Technologies**: Spring Boot, Spring WebFlux, Spring Data JPA, Security, Actuator, AOP, Eureka Client, OpenFeign, Resilience4j, Spring Kafka, JJWT, MapStruct, SpringDoc OpenAPI, Lombok.

---

## 1. Place Order

### API Name & Overview
**Place Order**
Creates a new order with status `PLACED` and payment status `PENDING`. It calculates totals, saves the order and order items, triggers an `OrderCreatedEvent` to Kafka, calls the LogisticsService to assign a pickup partner (with circuit breaker fallback), and sends a notification.

### HTTP Method
`POST`

### Endpoint URL
`/api/v1/orders`

### Full URL
`https://api.washgo.in/api/v1/orders`

### Authentication
Required

### Roles
Any authenticated user (typically CUSTOMER)

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token for authentication |
| `X-User-Id` | String | Yes | User ID injected by Gateway |
| `X-User-Role` | String | Yes | User role injected by Gateway |
| `Content-Type` | String | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "customerId": 101,
  "laundryPartnerId": 201,
  "pickupAddressId": 501,
  "paymentMethod": "UPI",
  "items": [
    {
      "serviceId": 1001,
      "serviceName": "Wash & Fold",
      "quantity": 2,
      "unitPrice": 50.00
    }
  ]
}
```

**Validation Rules**
| Field | Rule | Description |
| :--- | :--- | :--- |
| `customerId` | `@NotNull` | Must not be null |
| `laundryPartnerId` | `@NotNull` | Must not be null |
| `pickupAddressId` | `@NotNull` | Must not be null |
| `paymentMethod` | `@NotNull` | Must be a valid `PaymentMethod` (e.g. UPI, CARD, CASH_ON_DELIVERY) |
| `items` | `@NotNull` | List of items must not be null |

### Success Response
**Code: 201 Created**
```json
{
  "id": 5001,
  "orderNumber": "ORD-123456789",
  "customerId": 101,
  "laundryPartnerId": 201,
  "pickupAddressId": 501,
  "orderStatus": "PLACED",
  "paymentStatus": "PENDING",
  "paymentMethod": "UPI",
  "totalAmount": 100.00,
  "items": [
    {
      "id": 9001,
      "serviceId": 1001,
      "serviceName": "Wash & Fold",
      "quantity": 2,
      "unitPrice": 50.00,
      "totalPrice": 100.00
    }
  ],
  "createdAt": "2023-10-25T10:00:00Z",
  "updatedAt": "2023-10-25T10:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Validation failed for request body.
- **401 Unauthorized**: Missing or invalid token/gateway secrets.
- **403 Forbidden**: Insufficient privileges.
- **500 Internal Server Error**: Database or unexpected server error.

### Database Tables
- **Entity**: `Order` (table: `orders`), `OrderItem` (table: `order_items`)
- **Repository**: `OrderRepository`, `OrderItemRepository`
- **Operations**: Save Order and cascaded OrderItems.

### Service Flow
1. `OrderController` receives request.
2. `OrderServiceImpl.placeOrder` processes request, calculates totals, creates `Order` and `OrderItem` entities.
3. Order is saved via `OrderRepository`.
4. Produces `OrderCreatedEvent` to Kafka.
5. Calls `LogisticsIntegrationService.assignPickup` (Feign Client with Circuit Breaker).
6. Calls `NotificationClient.orderPlaced` to send notification.

### Events
- **Produces**: `OrderCreatedEvent` to `ORDER_CREATED` topic.

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 101,
    "laundryPartnerId": 201,
    "pickupAddressId": 501,
    "paymentMethod": "UPI",
    "items": [
      {
        "serviceId": 1001,
        "serviceName": "Wash & Fold",
        "quantity": 2,
        "unitPrice": 50.00
      }
    ]
  }'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer <token>',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    customerId: 101,
    laundryPartnerId: 201,
    pickupAddressId: 501,
    paymentMethod: "UPI",
    items: [{ serviceId: 1001, serviceName: "Wash & Fold", quantity: 2, unitPrice: 50.00 }]
  })
}).then(res => res.json()).then(console.log);
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/orders', {
  customerId: 101,
  laundryPartnerId: 201,
  pickupAddressId: 501,
  paymentMethod: "UPI",
  items: [{ serviceId: 1001, serviceName: "Wash & Fold", quantity: 2, unitPrice: 50.00 }]
}, {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => console.log(res.data));
```

### Java Example
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.washgo.in/api/v1/orders"))
    .header("Authorization", "Bearer <token>")
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString("{\"customerId\":101,...}"))
    .build();
HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
```

### Notes
- Logistics fallback is triggered if Logistics Service is down.

---

## 2. Get Order By ID

### API Name & Overview
**Get Order By ID**
Retrieves order details by its internal database ID.

### HTTP Method
`GET`

### Endpoint URL
`/api/v1/orders/{orderId}`

### Full URL
`https://api.washgo.in/api/v1/orders/{orderId}`

### Authentication
Required

### Roles
Any authenticated user

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `orderId` | Long | Yes | Internal ID of the order |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 200 OK**
```json
{
  "id": 5001,
  "orderNumber": "ORD-123456789",
  "customerId": 101,
  "orderStatus": "PLACED",
  "totalAmount": 100.00,
  "items": [ ... ]
}
```

### Error Responses
- **401 Unauthorized**: Missing token
- **404 Not Found**: Order not found (`ResourceNotFoundException`)
- **500 Internal Server Error**: Unexpected error

### Validation Rules
- Path variable must be a valid Long.

### Database Tables
- **Entity**: `Order`
- **Repository**: `OrderRepository.findById`

### Service Flow
1. `OrderController` receives GET request.
2. `OrderServiceImpl.getOrderById` fetches via `findById`.
3. Returns `OrderResponse`.

### Events
*None*

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/orders/5001 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders/5001', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => res.json()).then(console.log);
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/orders/5001', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => console.log(res.data));
```

### Java Example
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.washgo.in/api/v1/orders/5001"))
    .header("Authorization", "Bearer <token>")
    .GET()
    .build();
HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
```

### Notes
- Only the order owner or admin should ideally view the order (subject to authorization checks in service/gateway).

---

## 3. Get Order By Order Number

### API Name & Overview
**Get Order By Order Number**
Retrieves order details using the unique public order number.

### HTTP Method
`GET`

### Endpoint URL
`/api/v1/orders/number/{orderNumber}`

### Full URL
`https://api.washgo.in/api/v1/orders/number/{orderNumber}`

### Authentication
Required

### Roles
Any authenticated user

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `orderNumber` | String | Yes | Unique public order number (e.g. ORD-1234) |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 200 OK**
```json
{
  "id": 5001,
  "orderNumber": "ORD-123456789",
  "customerId": 101,
  "orderStatus": "PLACED",
  "totalAmount": 100.00,
  "items": [ ... ]
}
```

### Error Responses
- **401 Unauthorized**: Missing token
- **404 Not Found**: Order not found
- **500 Internal Server Error**: Unexpected error

### Validation Rules
- None specific beyond valid string.

### Database Tables
- **Entity**: `Order`
- **Repository**: `OrderRepository.findByOrderNumber`

### Service Flow
1. `OrderController` receives GET request.
2. `OrderServiceImpl.getOrderByOrderNumber` calls `findByOrderNumber`.
3. Maps entity to `OrderResponse`.

### Events
*None*

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/orders/number/ORD-123456789 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders/number/ORD-123456789', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => res.json()).then(console.log);
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/orders/number/ORD-123456789', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => console.log(res.data));
```

### Java Example
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.washgo.in/api/v1/orders/number/ORD-123456789"))
    .header("Authorization", "Bearer <token>")
    .GET()
    .build();
```

### Notes
- Useful for user-facing order tracking interfaces.

---

## 4. Get Customer Orders

### API Name & Overview
**Get Customer Orders**
Retrieves all orders associated with a specific customer ID.

### HTTP Method
`GET`

### Endpoint URL
`/api/v1/orders/customer/{customerId}`

### Full URL
`https://api.washgo.in/api/v1/orders/customer/{customerId}`

### Authentication
Required

### Roles
CUSTOMER, ADMIN

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `customerId` | Long | Yes | Customer ID |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 200 OK**
```json
[
  {
    "id": 5001,
    "orderNumber": "ORD-123456789",
    "customerId": 101,
    "orderStatus": "PLACED",
    "totalAmount": 100.00,
    "items": [ ... ]
  }
]
```

### Error Responses
- **401 Unauthorized**: Missing token
- **500 Internal Server Error**: Unexpected error

### Validation Rules
- Path variable must be a Long.

### Database Tables
- **Entity**: `Order`
- **Repository**: `OrderRepository.findByCustomerId`

### Service Flow
1. `OrderController` receives GET request.
2. `OrderServiceImpl.getCustomerOrders` calls `findByCustomerId`.
3. Returns List of `OrderResponse`.

### Events
*None*

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/orders/customer/101 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders/customer/101', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => res.json()).then(console.log);
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/orders/customer/101', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => console.log(res.data));
```

### Java Example
```java
// Similar to generic GET example
```

### Notes
- Returns an empty list `[]` if the customer has no orders.

---

## 5. Get Partner Orders

### API Name & Overview
**Get Partner Orders**
Retrieves all orders assigned to a specific laundry partner.

### HTTP Method
`GET`

### Endpoint URL
`/api/v1/orders/partner/{partnerId}`

### Full URL
`https://api.washgo.in/api/v1/orders/partner/{partnerId}`

### Authentication
Required

### Roles
LAUNDRY_PARTNER, ADMIN

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `partnerId` | Long | Yes | Laundry Partner ID |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 200 OK**
```json
[
  {
    "id": 5001,
    "orderNumber": "ORD-123456789",
    "laundryPartnerId": 201,
    "orderStatus": "PLACED",
    "totalAmount": 100.00
  }
]
```

### Error Responses
- **401 Unauthorized**: Missing token
- **500 Internal Server Error**: Unexpected error

### Validation Rules
- Path variable must be a Long.

### Database Tables
- **Entity**: `Order`
- **Repository**: `OrderRepository.findByLaundryPartnerId`

### Service Flow
1. `OrderController` receives GET request.
2. `OrderServiceImpl.getPartnerOrders` calls `findByLaundryPartnerId`.
3. Returns List of `OrderResponse`.

### Events
*None*

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/orders/partner/201 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders/partner/201', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => res.json()).then(console.log);
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/orders/partner/201', {
  headers: { 'Authorization': 'Bearer <token>' }
}).then(res => console.log(res.data));
```

### Java Example
```java
// Similar to generic GET example
```

### Notes
- Partner views their active/completed orders for processing.

---

## 6. Update Order Status

### API Name & Overview
**Update Order Status**
Updates the `orderStatus` of an existing order.

### HTTP Method
`PATCH`

### Endpoint URL
`/api/v1/orders/{orderId}/status`

### Full URL
`https://api.washgo.in/api/v1/orders/{orderId}/status`

### Authentication
Required

### Roles
ADMIN, LAUNDRY_PARTNER, DELIVERY_PARTNER

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |
| `Content-Type` | String | Yes | `application/json` |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `orderId` | Long | Yes | Internal Order ID |

### Query Parameters
*None*

### Request Body
```json
{
  "orderStatus": "PROCESSING"
}
```

**Validation Rules**
| Field | Rule | Description |
| :--- | :--- | :--- |
| `orderStatus` | `@NotNull` | Must be a valid `OrderStatus` enum |

### Success Response
**Code: 200 OK**
```json
{
  "id": 5001,
  "orderStatus": "PROCESSING",
  "updatedAt": "2023-10-25T11:00:00Z"
}
```

### Error Responses
- **400 Bad Request**: Invalid body validation
- **401 Unauthorized**: Missing token
- **404 Not Found**: Order ID not found
- **500 Internal Server Error**: Unexpected error

### Database Tables
- **Entity**: `Order`
- **Repository**: `OrderRepository.findById`, `OrderRepository.save`

### Service Flow
1. Fetch order by ID.
2. Update `orderStatus`.
3. Save back to DB.

### Events
*Could produce status update events, but currently straightforward update.*

### cURL Example
```bash
curl -X PATCH https://api.washgo.in/api/v1/orders/5001/status \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"orderStatus": "PROCESSING"}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders/5001/status', {
  method: 'PATCH',
  headers: {
    'Authorization': 'Bearer <token>',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ orderStatus: "PROCESSING" })
});
```

### Axios Example
```javascript
axios.patch('https://api.washgo.in/api/v1/orders/5001/status', {
  orderStatus: "PROCESSING"
}, {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP Client for PATCH
```

### Notes
- Status transitions should ideally be validated.

---

## 7. Health Check

### API Name & Overview
**Health Check**
Simple unauthenticated endpoint to verify if the Order Service is running.

### HTTP Method
`GET`

### Endpoint URL
`/api/v1/orders/health`

### Full URL
`https://api.washgo.in/api/v1/orders/health`

### Authentication
Not Required (permitAll in SecurityConfig)

### Roles
None

### Headers
*None*

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 200 OK**
```text
Order Service is UP
```

### Error Responses
- **500 Internal Server Error**: If the service itself is failing.

### Validation Rules
*None*

### Database Tables
*None*

### Service Flow
Returns static string from controller.

### Events
*None*

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/orders/health
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders/health').then(res => res.text()).then(console.log);
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/orders/health').then(res => console.log(res.data));
```

### Java Example
```java
// Simple GET
```

### Notes
- Actuator endpoints (`/actuator/**`) also provide deeper health insights.

---

## 8. Cancel Order

### API Name & Overview
**Cancel Order**
Marks an existing order as `CANCELLED`.

### HTTP Method
`DELETE`

### Endpoint URL
`/api/v1/orders/{orderId}`

### Full URL
`https://api.washgo.in/api/v1/orders/{orderId}`

### Authentication
Required

### Roles
CUSTOMER, ADMIN

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `orderId` | Long | Yes | Order ID to cancel |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 204 No Content**

### Error Responses
- **401 Unauthorized**: Missing token
- **404 Not Found**: Order ID not found
- **500 Internal Server Error**: Unexpected error

### Validation Rules
- Path variable must be a valid Long.

### Database Tables
- **Entity**: `Order`
- **Repository**: `OrderRepository.save` (Logical delete via status update)

### Service Flow
1. Fetch order.
2. Set `orderStatus = CANCELLED`.
3. Save order.

### Events
*None*

### cURL Example
```bash
curl -X DELETE https://api.washgo.in/api/v1/orders/5001 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/orders/5001', {
  method: 'DELETE',
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Axios Example
```javascript
axios.delete('https://api.washgo.in/api/v1/orders/5001', {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP Client for DELETE
```

### Notes
- Does not physically delete the record to maintain audit history.

---

## 9. Create Cart

### API Name & Overview
**Create Cart**
Initializes an empty shopping cart for a user and specific laundry partner.

### HTTP Method
`POST`

### Endpoint URL
`/api/v1/cart`

### Full URL
`https://api.washgo.in/api/v1/cart`

### Authentication
Required

### Roles
CUSTOMER

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |
| `Content-Type` | String | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "customerId": 101,
  "laundryPartnerId": 201
}
```

**Validation Rules**
| Field | Rule | Description |
| :--- | :--- | :--- |
| `customerId` | `@NotNull` | Must not be null |
| `laundryPartnerId` | `@NotNull` | Must not be null |

### Success Response
**Code: 201 Created**
```json
{
  "id": 1001,
  "customerId": 101,
  "laundryPartnerId": 201,
  "totalAmount": 0.00,
  "totalItems": 0,
  "cartItems": []
}
```

### Error Responses
- **400 Bad Request**: Validation failed
- **401 Unauthorized**: Missing token
- **500 Internal Server Error**: Unexpected error

### Database Tables
- **Entity**: `Cart`
- **Repository**: `CartRepository.save`

### Service Flow
1. `CartServiceImpl.createCart` checks logic, initializes new `Cart` entity.
2. Saves via `CartRepository`.
3. Returns `CartResponse`.

### Events
*None*

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/cart \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"customerId": 101, "laundryPartnerId": 201}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/cart', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer <token>',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ customerId: 101, laundryPartnerId: 201 })
});
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/cart', {
  customerId: 101,
  laundryPartnerId: 201
}, {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP Client for POST
```

### Notes
- Usually triggered implicitly when a customer first visits a partner's store.

---

## 10. Get Cart

### API Name & Overview
**Get Cart**
Retrieves the active cart for a specific customer.

### HTTP Method
`GET`

### Endpoint URL
`/api/v1/cart/{customerId}`

### Full URL
`https://api.washgo.in/api/v1/cart/{customerId}`

### Authentication
Required

### Roles
CUSTOMER

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `customerId` | Long | Yes | Customer ID |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 200 OK**
```json
{
  "id": 1001,
  "customerId": 101,
  "laundryPartnerId": 201,
  "totalAmount": 50.00,
  "totalItems": 1,
  "cartItems": [
    {
      "id": 3001,
      "serviceId": 1001,
      "serviceName": "Wash & Fold",
      "quantity": 1,
      "unitPrice": 50.00,
      "totalPrice": 50.00
    }
  ]
}
```

### Error Responses
- **401 Unauthorized**: Missing token
- **404 Not Found**: CartNotFoundException
- **500 Internal Server Error**: Unexpected error

### Validation Rules
- None specific.

### Database Tables
- **Entity**: `Cart`
- **Repository**: `CartRepository.findByCustomerId`

### Service Flow
1. Fetch cart by customer ID.
2. Throw `CartNotFoundException` if missing.
3. Map and return `CartResponse`.

### Events
*None*

### cURL Example
```bash
curl -X GET https://api.washgo.in/api/v1/cart/101 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/cart/101', {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Axios Example
```javascript
axios.get('https://api.washgo.in/api/v1/cart/101', {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP Client GET
```

### Notes
- A user typically has one active cart at a time.

---

## 11. Add Item to Cart

### API Name & Overview
**Add Item to Cart**
Adds a new item to an existing cart or updates quantity. Recalculates cart totals.

### HTTP Method
`POST`

### Endpoint URL
`/api/v1/cart/{customerId}/items`

### Full URL
`https://api.washgo.in/api/v1/cart/{customerId}/items`

### Authentication
Required

### Roles
CUSTOMER

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |
| `Content-Type` | String | Yes | `application/json` |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `customerId` | Long | Yes | Customer ID |

### Query Parameters
*None*

### Request Body
```json
{
  "serviceId": 1002,
  "serviceName": "Dry Cleaning",
  "quantity": 2,
  "unitPrice": 100.00
}
```

**Validation Rules**
| Field | Rule | Description |
| :--- | :--- | :--- |
| `serviceId` | `@NotNull` | Must not be null |
| `serviceName` | `@NotNull`, `@NotBlank` | Name of the service |
| `quantity` | `@NotNull`, `@Min(1)` | Quantity must be >= 1 |
| `unitPrice` | `@NotNull` | Positive price |

### Success Response
**Code: 200 OK**
```json
{
  "id": 1001,
  "customerId": 101,
  "laundryPartnerId": 201,
  "totalAmount": 250.00,
  "totalItems": 3,
  "cartItems": [ ... ]
}
```

### Error Responses
- **400 Bad Request**: Invalid body validation
- **401 Unauthorized**: Missing token
- **404 Not Found**: CartNotFoundException
- **500 Internal Server Error**: Unexpected error

### Database Tables
- **Entity**: `Cart`, `CartItem`
- **Repository**: `CartRepository.save`

### Service Flow
1. Fetch cart by customer ID.
2. Calculate `totalPrice` for new item (`quantity * unitPrice`).
3. Add `CartItem` to cart.
4. Update `totalAmount` and `totalItems` on `Cart`.
5. Save `Cart`.

### Events
*None*

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/cart/101/items \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"serviceId": 1002, "serviceName": "Dry Cleaning", "quantity": 2, "unitPrice": 100.00}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/cart/101/items', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer <token>',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ serviceId: 1002, serviceName: "Dry Cleaning", quantity: 2, unitPrice: 100.00 })
});
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/cart/101/items', {
  serviceId: 1002, serviceName: "Dry Cleaning", quantity: 2, unitPrice: 100.00
}, {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP POST
```

### Notes
- Will create a new `CartItem` entity bound to the `Cart`.

---

## 12. Remove Item from Cart

### API Name & Overview
**Remove Item from Cart**
Removes a specific item from a cart based on `cartItemId`.

### HTTP Method
`DELETE`

### Endpoint URL
`/api/v1/cart/items/{cartItemId}`

### Full URL
`https://api.washgo.in/api/v1/cart/items/{cartItemId}`

### Authentication
Required

### Roles
CUSTOMER

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `cartItemId` | Long | Yes | ID of the cart item to remove |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 200 OK**
Returns updated `CartResponse`.

### Error Responses
- **401 Unauthorized**: Missing token
- **404 Not Found**: Item or Cart not found
- **500 Internal Server Error**: Unexpected error

### Database Tables
- **Entity**: `Cart`, `CartItem`
- **Repository**: `CartRepository.save`, `CartItemRepository.delete`

### Service Flow
1. Fetch cart and cart item.
2. Remove item from cart's list.
3. Recalculate totals.
4. Save cart.

### Events
*None*

### cURL Example
```bash
curl -X DELETE https://api.washgo.in/api/v1/cart/items/3001 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/cart/items/3001', {
  method: 'DELETE',
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Axios Example
```javascript
axios.delete('https://api.washgo.in/api/v1/cart/items/3001', {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP DELETE
```

### Notes
- Updates the total cart calculations accordingly.

---

## 13. Clear Cart

### API Name & Overview
**Clear Cart**
Empties all items from a user's cart and resets totals to zero.

### HTTP Method
`DELETE`

### Endpoint URL
`/api/v1/cart/{customerId}`

### Full URL
`https://api.washgo.in/api/v1/cart/{customerId}`

### Authentication
Required

### Roles
CUSTOMER

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |

### Path Parameters
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `customerId` | Long | Yes | Customer ID |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
**Code: 204 No Content**

### Error Responses
- **401 Unauthorized**: Missing token
- **404 Not Found**: CartNotFoundException
- **500 Internal Server Error**: Unexpected error

### Database Tables
- **Entity**: `Cart`, `CartItem`
- **Repository**: `CartRepository.save`

### Service Flow
1. Fetch cart by customer ID.
2. Clear `cartItems` list.
3. Reset `totalAmount` and `totalItems` to zero.
4. Save cart.

### Events
*None*

### cURL Example
```bash
curl -X DELETE https://api.washgo.in/api/v1/cart/101 \
  -H "Authorization: Bearer <token>"
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/cart/101', {
  method: 'DELETE',
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Axios Example
```javascript
axios.delete('https://api.washgo.in/api/v1/cart/101', {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP DELETE
```

### Notes
- Usually called after successful order placement or manual clear action.

---

## 14. Assign Pickup Partner (Logistics Inter-Service)

### API Name & Overview
**Assign Pickup Partner**
Endpoint accessed by Order Service/System to assign logistics for pickup. Exposed under the `/api/v1/logistics` prefix in this service (acting as a facade or direct logistics endpoint).

### HTTP Method
`POST`

### Endpoint URL
`/api/v1/logistics/pickup`

### Full URL
`https://api.washgo.in/api/v1/logistics/pickup`

### Authentication
Required (Often secured via internal gateway scopes/secrets)

### Roles
SYSTEM, ADMIN

### Headers
| Header Name | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Yes | Bearer token |
| `Content-Type` | String | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "orderId": 5001,
  "pickupAddressId": 501,
  "laundryPartnerId": 201
}
```

**Validation Rules**
| Field | Rule | Description |
| :--- | :--- | :--- |
| `orderId` | `@NotNull` | Internal ID of the order |
| `pickupAddressId` | `@NotNull` | Location for pickup |
| `laundryPartnerId` | `@NotNull` | Partner destination |

### Success Response
**Code: 200 OK**
```json
{
  "deliveryId": 8001,
  "status": "ASSIGNED",
  "deliveryPartnerId": 901
}
```

### Error Responses
- **400 Bad Request**: Invalid body
- **401 Unauthorized**: Missing token
- **500 Internal Server Error**: Logistics assignment failed

### Database Tables
- **Entity**: None directly in Order Service (Interacts with Logistics DB)

### Service Flow
1. Acts as logistics handler.
2. Usually forwards or maps to Logistics Integration.

### Events
*None*

### cURL Example
```bash
curl -X POST https://api.washgo.in/api/v1/logistics/pickup \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"orderId": 5001, "pickupAddressId": 501, "laundryPartnerId": 201}'
```

### JavaScript Fetch Example
```javascript
fetch('https://api.washgo.in/api/v1/logistics/pickup', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer <token>',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ orderId: 5001, pickupAddressId: 501, laundryPartnerId: 201 })
});
```

### Axios Example
```javascript
axios.post('https://api.washgo.in/api/v1/logistics/pickup', {
  orderId: 5001, pickupAddressId: 501, laundryPartnerId: 201
}, {
  headers: { 'Authorization': 'Bearer <token>' }
});
```

### Java Example
```java
// Java HTTP Client
```

### Notes
- Called internally by `LogisticsIntegrationService` using `@CircuitBreaker` and OpenFeign.

---

## Endpoint Summary

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/orders` | Place a new order | Yes |
| `GET` | `/api/v1/orders/{orderId}` | Get order by internal ID | Yes |
| `GET` | `/api/v1/orders/number/{orderNumber}` | Get order by public number | Yes |
| `GET` | `/api/v1/orders/customer/{customerId}`| Get all orders for a customer | Yes |
| `GET` | `/api/v1/orders/partner/{partnerId}` | Get all orders for a laundry partner | Yes |
| `PATCH` | `/api/v1/orders/{orderId}/status` | Update status of an order | Yes |
| `GET` | `/api/v1/orders/health` | Health check endpoint | No |
| `DELETE` | `/api/v1/orders/{orderId}` | Cancel an existing order | Yes |
| `POST` | `/api/v1/cart` | Create a new empty cart | Yes |
| `GET` | `/api/v1/cart/{customerId}` | Retrieve active cart for customer | Yes |
| `POST` | `/api/v1/cart/{customerId}/items` | Add or update item in cart | Yes |
| `DELETE` | `/api/v1/cart/items/{cartItemId}` | Remove item from cart | Yes |
| `DELETE` | `/api/v1/cart/{customerId}` | Clear all items from cart | Yes |
| `POST` | `/api/v1/logistics/pickup` | Assign logistics partner | Yes |
