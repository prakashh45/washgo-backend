# Notification Service

## Overview
The Notification Service handles sending communications (Email, SMS, Push) to users regarding order statuses and other events.

- **Name**: NOTIFICATION-SERVICE
- **Port**: 8086
- **Base URL**: `/api/v1/notifications`
- **Database**: PostgreSQL (`washgo_notification`)
- **Email SMTP**: smtp.gmail.com:587

---

## 1. Order Placed Notification & Overview
Sends a notification when an order is placed. Currently ONLY prints to System.out and returns a success message without calling NotificationService.

- **HTTP Method**: POST
- **Endpoint URL**: `/api/v1/notifications/order-placed`
- **Full URL**: `https://api.washgo.in/api/v1/notifications/order-placed`
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
  "userId": "usr_123",
  "recipient": "user@example.com",
  "title": "Order Placed",
  "message": "Your order has been successfully placed.",
  "type": "EMAIL"
}
```
| Field | Type | Required | Description |
|---|---|---|---|
| userId | String | Yes | ID of the user |
| recipient | String | Yes | Email or phone |
| title | String | Yes | Notification title |
| message | String | Yes | Notification body content |
| type | String | Yes | Enum: EMAIL, SMS, PUSH, IN_APP |

### Success Response
```json
"Notification Sent Successfully"
```

### Error Responses
- **400 Bad Request**: MethodArgumentNotValidException
- **404 Not Found**: EntityNotFoundException
- **500 Internal Server Error**: Exception

### Validation Rules
- `userId`: @NotBlank
- `recipient`: @NotBlank
- `title`: @NotBlank
- `message`: @NotBlank
- `type`: @NotNull

### Database Tables
- **Entity**: `Notification` (table: `notifications`), `ProcessedEvent` (table: `processed_events`)
- **Repository**: `NotificationRepository`, `ProcessedEventRepository`
- **Operations**: *None in this endpoint currently (TODO).*

### Service Flow
`NotificationController` → `System.out.println()`

### Events
- **Kafka**: `OrderCreatedConsumer` listens to `ORDER_CREATED_TOPIC` (group: `NOTIFICATION_GROUP`). Implements idempotency via `ProcessedEventRepository`. Contains TODO for save/send. Uses `DeadLetterPublishingRecoverer` to `ORDER_CREATED_DLT_TOPIC`.

### cURL example
```bash
curl -X POST https://api.washgo.in/api/v1/notifications/order-placed \
  -H "Content-Type: application/json" \
  -d '{"userId":"usr_123","recipient":"user@example.com","title":"Order Placed","message":"Your order has been successfully placed.","type":"EMAIL"}'
```

### JavaScript Fetch example
```javascript
fetch("https://api.washgo.in/api/v1/notifications/order-placed", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ userId: "usr_123", recipient: "user@example.com", title: "Order Placed", message: "Your order has been successfully placed.", type: "EMAIL" })
});
```

### Axios example
```javascript
axios.post("https://api.washgo.in/api/v1/notifications/order-placed", {
  userId: "usr_123",
  recipient: "user@example.com",
  title: "Order Placed",
  message: "Your order has been successfully placed.",
  type: "EMAIL"
});
```

### Java example
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/v1/notifications/order-placed"))
  .header("Content-Type", "application/json")
  .POST(BodyPublishers.ofString("{\"userId\":\"usr_123\",\"recipient\":\"user@example.com\",\"title\":\"Order Placed\",\"message\":\"Your order has been successfully placed.\",\"type\":\"EMAIL\"}"))
  .build();
```

### Notes
- TODO - The endpoint currently only prints to standard output and does not call the NotificationService.

---

## Endpoint Summary
| API Name | Method | Path | Description |
|---|---|---|---|
| Order Placed Notification | POST | `/api/v1/notifications/order-placed` | Triggers order placed notification |
