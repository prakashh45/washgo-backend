# Logistics Service

## Overview
The Logistics Service is responsible for managing delivery partners and their delivery assignments (pickup and delivery legs) for orders.

- **Name**: logistics-service
- **Port**: 8085
- **Base URLs**: `/api/v1/assignments`, `/api/v1/delivery-partners`
- **Database**: PostgreSQL

---

## 1. Assign Partner & Overview
Assigns an active delivery partner to an order for a specific delivery leg.

- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/assignments`
- **Full URL**: `https://api.washgo.in/api/v1/assignments`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

### Path Parameters
*None*

### Query Parameters
*None*

### Request Body
```json
{
  "orderId": 12345,
  "deliveryPartnerId": 1,
  "legType": "PICKUP"
}
```
| Field | Type | Required | Description |
|---|---|---|---|
| orderId | Long | Yes | The ID of the order |
| deliveryPartnerId | Long | Yes | ID of the delivery partner |
| legType | String | Yes | Enum: PICKUP, DELIVERY |

### Success Response
```json
{
  "id": 1,
  "orderId": 12345,
  "deliveryPartnerId": 1,
  "deliveryPartnerName": "John Doe",
  "legType": "PICKUP",
  "status": "ASSIGNED",
  "assignedAt": "2023-10-10T10:00:00",
  "acceptedAt": null,
  "startedAt": null,
  "arrivedAt": null,
  "completedAt": null
}
```

### Error Responses
- **400 Bad Request**: Invalid body / BusinessException
- **404 Not Found**: DeliveryPartnerNotFoundException
- **500 Internal Server Error**: Generic exception

### Validation Rules
- `orderId`: @NotNull
- `deliveryPartnerId`: @NotNull
- `legType`: @NotNull

### Database Tables
- **Entity**: `DeliveryAssignment` (table: `delivery_assignments`)
- **Repository**: `DeliveryAssignmentRepository`
- **Operations**: `save()`

### Service Flow
`DeliveryAssignmentController` → `DeliveryAssignmentServiceImpl.assignPartner()` → `DeliveryAssignmentRepository.save()` → Database

### Events
- **Kafka**: `OrderCreatedConsumer` listens to `order_created` topic.

### cURL example
```bash
curl -X POST https://api.washgo.in/api/v1/assignments \
  -H "Content-Type: application/json" \
  -d '{"orderId":12345,"deliveryPartnerId":1,"legType":"PICKUP"}'
```

### JavaScript Fetch example
```javascript
fetch("https://api.washgo.in/api/v1/assignments", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ orderId: 12345, deliveryPartnerId: 1, legType: "PICKUP" })
});
```

### Axios example
```javascript
axios.post("https://api.washgo.in/api/v1/assignments", {
  orderId: 12345,
  deliveryPartnerId: 1,
  legType: "PICKUP"
});
```

### Java example
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/v1/assignments"))
  .header("Content-Type", "application/json")
  .POST(BodyPublishers.ofString("{\"orderId\":12345,\"deliveryPartnerId\":1,\"legType\":\"PICKUP\"}"))
  .build();
```

### Notes
- Defaults status to ASSIGNED.

---

## 2. Get Assignment & Overview
Retrieves a specific delivery assignment by its ID.

- **HTTP Method**: GET
- **Endpoint URL**: `/api/v1/assignments/{id}`
- **Full URL**: `https://api.washgo.in/api/v1/assignments/{id}`
- **Authentication**: Not Required
- **Roles**: None

### Headers
| Header | Type | Required | Description |
|---|---|---|---|
| Accept | string | No | `application/json` |

### Path Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| id | Long | Yes | Assignment ID |

### Query Parameters
*None*

### Request Body
*None*

### Success Response
```json
{
  "id": 1,
  "orderId": 12345,
  "deliveryPartnerId": 1,
  "deliveryPartnerName": "John Doe",
  "legType": "PICKUP",
  "status": "ASSIGNED"
}
```

### Error Responses
- **404 Not Found**: ResourceNotFoundException
- **500 Internal Server Error**: Exception

### Validation Rules
- `id`: Must be valid long.

### Database Tables
- **Entity**: `DeliveryAssignment` (table: `delivery_assignments`)
- **Repository**: `DeliveryAssignmentRepository`
- **Operations**: `findById()`

### Service Flow
`DeliveryAssignmentController` → `DeliveryAssignmentServiceImpl.getAssignment()` → `DeliveryAssignmentRepository.findById()` → Database

### Events
*None*

### cURL example
```bash
curl -X GET https://api.washgo.in/api/v1/assignments/1
```

### JavaScript Fetch example
```javascript
fetch("https://api.washgo.in/api/v1/assignments/1");
```

### Axios example
```javascript
axios.get("https://api.washgo.in/api/v1/assignments/1");
```

### Java example
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/v1/assignments/1"))
  .GET()
  .build();
```

### Notes
- TODO - not found in code.

---
*(Note: Remaining endpoints 3-17 follow the exact same structure for brevity. See Endpoint Summary below for all endpoints.)*

## Endpoint Summary
| API Name | Method | Path | Description |
|---|---|---|---|
| Assign Partner | POST | `/api/v1/assignments` | Assign a delivery partner |
| Get Assignment | GET | `/api/v1/assignments/{id}` | Get assignment by ID |
| Get Assignments By Order | GET | `/api/v1/assignments/order/{orderId}` | Get assignments by Order ID |
| Get Assignments By Partner | GET | `/api/v1/assignments/partner/{partnerId}` | Get assignments by Partner ID |
| Accept Assignment | PUT | `/api/v1/assignments/{id}/accept` | Accept an assignment |
| Reject Assignment | PUT | `/api/v1/assignments/{id}/reject` | Reject an assignment |
| Start Trip | PUT | `/api/v1/assignments/{id}/start` | Start the delivery trip |
| Arrived | PUT | `/api/v1/assignments/{id}/arrived` | Mark as arrived |
| Verify OTP | PUT | `/api/v1/assignments/{id}/verify-otp` | Verify OTP |
| Complete Assignment | PUT | `/api/v1/assignments/{id}/complete` | Complete assignment |
| Get Partner By Id | GET | `/api/v1/delivery-partners/{id}` | Get partner details |
| Get All Partners | GET | `/api/v1/delivery-partners/` | List all partners |
| Create Partner | POST | `/api/v1/delivery-partners/` | Register new partner |
| Update Partner | PUT | `/api/v1/delivery-partners/{id}` | Update partner details |
| Update Availability | PATCH | `/api/v1/delivery-partners/{id}/availability` | Update availability status |
| Update Location | PATCH | `/api/v1/delivery-partners/{id}/location` | Update GPS location |
| Update Status | PATCH | `/api/v1/delivery-partners/{id}/status` | Update partner account status |
