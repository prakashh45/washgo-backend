# Authentication Service (auth-service)

## Overview
- **Name**: auth-service
- **Port**: 8081
- **Base URLs**: `/api/v1/auth` (public), `/internal/users` (internal)
- **Package**: `com.washgo.auth`
- **Database**: PostgreSQL `washgo_db` (`jdbc:postgresql://localhost:5432/washgo_db`)
- **Authentication**: Firebase Admin SDK

---

## Endpoint: Sync User (Frontend)

### API Name & Overview
Sync User. Authenticates a user using their Firebase ID token and syncs them into the local PostgreSQL database. If the user doesn't exist, a new user is created with `Role.CUSTOMER`.

### HTTP Method
POST

### Endpoint URL
`/api/v1/auth/sync`

### Full URL
`https://api.washgo.in/api/v1/auth/sync`

### Authentication
Required (Firebase Bearer Token)

### Roles
Any Valid Authenticated User (ROLE_USER authority set by filter)

### Headers table
| Header | Type | Required | Description |
|---|---|---|---|
| Authorization | String | Yes | Bearer token containing the Firebase ID Token |

### Path Parameters table
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | - | - | - |

### Query Parameters table
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | - | - | - |

### Request Body
None

### Success Response
**Status: 200 OK**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "firebaseUid": "abc123firebaseUID",
  "email": "user@example.com",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890",
  "profileImage": "https://example.com/avatar.jpg",
  "role": "CUSTOMER"
}
```

### Error Responses
- **401 Unauthorized**: Invalid or missing Firebase token.
- **500 Internal Server Error**: Server-side error during synchronization.

### Validation Rules
TODO - not found in code

### Database Tables
- **Entity**: `User` (table: `users`)
- **Repository**: `UserRepository`
- **Operations**: `findByFirebaseUid`, save (create if not found)

### Service Flow
`AuthController` → verifies token using `FirebaseAuth.getInstance().verifyIdToken()` → `UserService.createOrGetUser` → `UserRepository` → PostgreSQL Database.

### Events
None

### cURL example
```bash
curl -X POST https://api.washgo.in/api/v1/auth/sync \
  -H "Authorization: Bearer YOUR_FIREBASE_ID_TOKEN"
```

### JavaScript Fetch example
```javascript
fetch('https://api.washgo.in/api/v1/auth/sync', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer YOUR_FIREBASE_ID_TOKEN'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

### Axios example
```javascript
const axios = require('axios');

axios.post('https://api.washgo.in/api/v1/auth/sync', {}, {
  headers: {
    'Authorization': 'Bearer YOUR_FIREBASE_ID_TOKEN'
  }
})
.then(response => console.log(response.data))
.catch(error => console.error(error));
```

### Java example
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/v1/auth/sync"))
  .header("Authorization", "Bearer YOUR_FIREBASE_ID_TOKEN")
  .POST(HttpRequest.BodyPublishers.noBody())
  .build();

HttpResponse<String> response = HttpClient.newHttpClient()
  .send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
```

### Notes
- The `FirebaseTokenFilter` handles token extraction and verification before hitting the controller.
- `fullName` defaults to "Unknown User" if null during creation.

---

## Endpoint: Health Check

### API Name & Overview
Auth Service Health Check. Used to verify that the authentication service is running and accessible.

### HTTP Method
GET

### Endpoint URL
`/api/v1/auth/health`

### Full URL
`https://api.washgo.in/api/v1/auth/health`

### Authentication
Not Required

### Roles
None

### Headers table
| Header | Type | Required | Description |
|---|---|---|---|
| None | - | - | - |

### Path Parameters table
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | - | - | - |

### Query Parameters table
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | - | - | - |

### Request Body
None

### Success Response
**Status: 200 OK**
```text
Auth Service Running
```

### Error Responses
- **500 Internal Server Error**: Service is down or experiencing issues.

### Validation Rules
None

### Database Tables
None

### Service Flow
`AuthController` → Returns plain string.

### Events
None

### cURL example
```bash
curl -X GET https://api.washgo.in/api/v1/auth/health
```

### JavaScript Fetch example
```javascript
fetch('https://api.washgo.in/api/v1/auth/health')
  .then(response => response.text())
  .then(data => console.log(data));
```

### Axios example
```javascript
const axios = require('axios');

axios.get('https://api.washgo.in/api/v1/auth/health')
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
```

### Java example
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/v1/auth/health"))
  .GET()
  .build();

HttpResponse<String> response = HttpClient.newHttpClient()
  .send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
```

### Notes
- This endpoint is permitted to all in `SecurityConfig`.

---

## Endpoint: Internal Sync User

### API Name & Overview
Internal Sync User. An internal API endpoint called by the API Gateway or other microservices to synchronize a user profile.

### HTTP Method
POST

### Endpoint URL
`/internal/users/sync`

### Full URL
`https://api.washgo.in/internal/users/sync`

### Authentication
Internal only (Typically handled via Gateway routing or mTLS, not exposed to public).

### Roles
Internal Service

### Headers table
| Header | Type | Required | Description |
|---|---|---|---|
| Content-Type | String | Yes | application/json |

### Path Parameters table
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | - | - | - |

### Query Parameters table
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | - | - | - |

### Request Body
```json
{
  "firebaseUid": "abc123firebaseUID",
  "email": "user@example.com",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890",
  "profileImage": "https://example.com/avatar.jpg"
}
```

**Validation Rules**
| Field | Type | Annotations | Description |
|---|---|---|---|
| firebaseUid | String | TODO - not found in code | The Firebase UID of the user |
| email | String | TODO - not found in code | Email address |
| fullName | String | TODO - not found in code | Full Name |
| phoneNumber | String | TODO - not found in code | Phone Number |
| profileImage | String | TODO - not found in code | Profile Image URL |

### Success Response
**Status: 200 OK**
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "firebaseUid": "abc123firebaseUID",
  "role": "CUSTOMER",
  "active": true
}
```

### Error Responses
- **400 Bad Request**: Invalid request body formatting.
- **500 Internal Server Error**: Database or synchronization error.

### Validation Rules
Requires `@Valid` on the `@RequestBody`, but explicit property-level validation annotations (e.g., `@NotBlank`, `@Email`) were not found in the source code data provided.

### Database Tables
- **Entity**: `User` (table: `users`)
- **Repository**: `UserRepository`

### Service Flow
`InternalUserController` → `UserService.syncUser` → `UserService.createOrGetUser` → `UserRepository` → PostgreSQL Database.

### Events
None

### cURL example
```bash
curl -X POST https://api.washgo.in/internal/users/sync \
  -H "Content-Type: application/json" \
  -d '{
    "firebaseUid": "abc123firebaseUID",
    "email": "user@example.com",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890",
    "profileImage": "https://example.com/avatar.jpg"
  }'
```

### JavaScript Fetch example
```javascript
fetch('https://api.washgo.in/internal/users/sync', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    firebaseUid: "abc123firebaseUID",
    email: "user@example.com",
    fullName: "John Doe",
    phoneNumber: "+1234567890",
    profileImage: "https://example.com/avatar.jpg"
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

### Axios example
```javascript
const axios = require('axios');

axios.post('https://api.washgo.in/internal/users/sync', {
  firebaseUid: "abc123firebaseUID",
  email: "user@example.com",
  fullName: "John Doe",
  phoneNumber: "+1234567890",
  profileImage: "https://example.com/avatar.jpg"
})
.then(response => console.log(response.data))
.catch(error => console.error(error));
```

### Java example
```java
String jsonBody = """
{
  "firebaseUid": "abc123firebaseUID",
  "email": "user@example.com",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890",
  "profileImage": "https://example.com/avatar.jpg"
}
""";

HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/internal/users/sync"))
  .header("Content-Type", "application/json")
  .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
  .build();

HttpResponse<String> response = HttpClient.newHttpClient()
  .send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
```

### Notes
- Returns `SyncUserResponse` record.
- Used strictly for inter-service communication (e.g., API Gateway delegating a sync operation).

---

## Endpoint Summary

| Method | Endpoint | Description | Auth Required | Roles |
|---|---|---|---|---|
| POST | `/api/v1/auth/sync` | Syncs user with Firebase ID Token | Yes (Firebase Token) | Any Authenticated |
| GET | `/api/v1/auth/health` | Auth service health check | No | None |
| POST | `/internal/users/sync` | Internally syncs user details | Internal Only | Internal Service |
