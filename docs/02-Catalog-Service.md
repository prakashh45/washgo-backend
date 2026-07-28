# Catalog Service API Documentation

## Service Overview
- **Name**: catalog-service / CATALOG-SERVICE
- **Port**: 8081
- **Base URL**: `/api/catalog`
- **Package**: `com.washgo.catalog`
- **Database**: PostgreSQL `washgo_catalog_db` (`jdbc:postgresql://localhost:5432/washgo_catalog_db`)
- **Dependencies**: spring-boot-starter-web, data-jpa, eureka-client, config, postgresql, lombok

---

## 1. Create Laundry Partner

**API Name & Overview**
Create Laundry Partner. This endpoint is used to register a new laundry partner (shop) in the system.

**HTTP Method**
`POST`

**Endpoint URL**
`/api/catalog/partners`

**Full URL**
`https://api.washgo.in/api/catalog/partners`

**Authentication**
Not Required

**Roles**
TODO - not found in code

**Headers**
| Header | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

**Path Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Query Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Request Body**
```json
{
  "shopName": "Sparkle Laundry",
  "ownerName": "John Doe",
  "phoneNumber": "9876543210",
  "email": "contact@sparklelaundry.com",
  "address": "123 Main Street",
  "city": "Mumbai",
  "latitude": 19.0760,
  "longitude": 72.8777,
  "coverImage": "https://example.com/images/sparkle.jpg"
}
```

**Success Response**
HTTP Status: `201 Created`
```json
{
  "id": 1,
  "shopName": "Sparkle Laundry",
  "ownerName": "John Doe",
  "phoneNumber": "9876543210",
  "email": "contact@sparklelaundry.com",
  "address": "123 Main Street",
  "city": "Mumbai",
  "latitude": 19.0760,
  "longitude": 72.8777,
  "coverImage": "https://example.com/images/sparkle.jpg",
  "verified": false,
  "holidayMode": false,
  "averageRating": 0.0,
  "totalReviews": 0,
  "active": true
}
```

**Error Responses**
- `500 Internal Server Error`: Thrown when a `RuntimeException` occurs or database constraint violates (e.g., duplicate phone/email).

**Validation Rules**
TODO - not found in code (No validation annotations on DTOs)

**Database Tables**
- **Entity**: `LaundryPartner`
- **Table**: `laundry_partners`
- **Operations**: Insert new record. `@PrePersist` sets `createdAt`.

**Service Flow**
`LaundryPartnerController` → `LaundryPartnerServiceImpl.createPartner` → `LaundryPartnerRepository.save` → Database

**Events**
TODO - not found in code

**cURL example**
```bash
curl -X POST https://api.washgo.in/api/catalog/partners \
  -H "Content-Type: application/json" \
  -d '{
    "shopName": "Sparkle Laundry",
    "ownerName": "John Doe",
    "phoneNumber": "9876543210",
    "email": "contact@sparklelaundry.com",
    "address": "123 Main Street",
    "city": "Mumbai",
    "latitude": 19.0760,
    "longitude": 72.8777,
    "coverImage": "https://example.com/images/sparkle.jpg"
  }'
```

**JavaScript Fetch example**
```javascript
fetch("https://api.washgo.in/api/catalog/partners", {
  method: "POST",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify({
    shopName: "Sparkle Laundry",
    ownerName: "John Doe",
    phoneNumber: "9876543210",
    email: "contact@sparklelaundry.com",
    address: "123 Main Street",
    city: "Mumbai",
    latitude: 19.0760,
    longitude: 72.8777,
    coverImage: "https://example.com/images/sparkle.jpg"
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

**Axios example**
```javascript
axios.post("https://api.washgo.in/api/catalog/partners", {
  shopName: "Sparkle Laundry",
  ownerName: "John Doe",
  phoneNumber: "9876543210",
  email: "contact@sparklelaundry.com",
  address: "123 Main Street",
  city: "Mumbai",
  latitude: 19.0760,
  longitude: 72.8777,
  coverImage: "https://example.com/images/sparkle.jpg"
})
.then(response => console.log(response.data));
```

**Java example**
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/catalog/partners"))
  .header("Content-Type", "application/json")
  .POST(HttpRequest.BodyPublishers.ofString("{\"shopName\":\"Sparkle Laundry\",\"ownerName\":\"John Doe\",\"phoneNumber\":\"9876543210\",\"email\":\"contact@sparklelaundry.com\",\"address\":\"123 Main Street\",\"city\":\"Mumbai\",\"latitude\":19.076,\"longitude\":72.8777,\"coverImage\":\"https://example.com/images/sparkle.jpg\"}"))
  .build();

HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
```

**Notes**
`verified`, `holidayMode`, `averageRating`, `totalReviews`, and `active` are initialized with default values.

---

## 2. Get All Laundry Partners

**API Name & Overview**
Get All Laundry Partners. Retrieves a list of all laundry partners in the system.

**HTTP Method**
`GET`

**Endpoint URL**
`/api/catalog/partners`

**Full URL**
`https://api.washgo.in/api/catalog/partners`

**Authentication**
Not Required

**Roles**
TODO - not found in code

**Headers**
| Header | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Path Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Query Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Request Body**
None

**Success Response**
HTTP Status: `200 OK`
```json
[
  {
    "id": 1,
    "shopName": "Sparkle Laundry",
    "ownerName": "John Doe",
    "phoneNumber": "9876543210",
    "email": "contact@sparklelaundry.com",
    "address": "123 Main Street",
    "city": "Mumbai",
    "latitude": 19.0760,
    "longitude": 72.8777,
    "coverImage": "https://example.com/images/sparkle.jpg",
    "verified": false,
    "holidayMode": false,
    "averageRating": 0.0,
    "totalReviews": 0,
    "active": true
  }
]
```

**Error Responses**
- `500 Internal Server Error`: Thrown on generic database or server errors.

**Validation Rules**
TODO - not found in code

**Database Tables**
- **Entity**: `LaundryPartner`
- **Table**: `laundry_partners`
- **Operations**: Select all records.

**Service Flow**
`LaundryPartnerController` → `LaundryPartnerServiceImpl.getAllPartners` → `LaundryPartnerRepository.findAll` → Database

**Events**
TODO - not found in code

**cURL example**
```bash
curl -X GET https://api.washgo.in/api/catalog/partners
```

**JavaScript Fetch example**
```javascript
fetch("https://api.washgo.in/api/catalog/partners")
  .then(response => response.json())
  .then(data => console.log(data));
```

**Axios example**
```javascript
axios.get("https://api.washgo.in/api/catalog/partners")
  .then(response => console.log(response.data));
```

**Java example**
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/catalog/partners"))
  .GET()
  .build();

HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
```

**Notes**
Returns an empty array if no partners exist.

---

## 3. Get Laundry Partner By ID

**API Name & Overview**
Get Laundry Partner By ID. Fetches details of a specific laundry partner.

**HTTP Method**
`GET`

**Endpoint URL**
`/api/catalog/partners/{id}`

**Full URL**
`https://api.washgo.in/api/catalog/partners/{id}`

**Authentication**
Not Required

**Roles**
TODO - not found in code

**Headers**
| Header | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Path Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | Long | Yes | ID of the laundry partner |

**Query Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Request Body**
None

**Success Response**
HTTP Status: `200 OK`
```json
{
  "id": 1,
  "shopName": "Sparkle Laundry",
  "ownerName": "John Doe",
  "phoneNumber": "9876543210",
  "email": "contact@sparklelaundry.com",
  "address": "123 Main Street",
  "city": "Mumbai",
  "latitude": 19.0760,
  "longitude": 72.8777,
  "coverImage": "https://example.com/images/sparkle.jpg",
  "verified": true,
  "holidayMode": false,
  "averageRating": 4.5,
  "totalReviews": 120,
  "active": true
}
```

**Error Responses**
- `500 Internal Server Error`: `RuntimeException` thrown if the partner is not found.

**Validation Rules**
TODO - not found in code

**Database Tables**
- **Entity**: `LaundryPartner`
- **Table**: `laundry_partners`
- **Operations**: Select record by ID.

**Service Flow**
`LaundryPartnerController` → `LaundryPartnerServiceImpl.getPartnerById` → `LaundryPartnerRepository.findById` → Database

**Events**
TODO - not found in code

**cURL example**
```bash
curl -X GET https://api.washgo.in/api/catalog/partners/1
```

**JavaScript Fetch example**
```javascript
fetch("https://api.washgo.in/api/catalog/partners/1")
  .then(response => response.json())
  .then(data => console.log(data));
```

**Axios example**
```javascript
axios.get("https://api.washgo.in/api/catalog/partners/1")
  .then(response => console.log(response.data));
```

**Java example**
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/catalog/partners/1"))
  .GET()
  .build();

HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
```

**Notes**
Throws a standard `RuntimeException` without a custom exception handler if the ID does not exist.

---

## 4. Create Laundry Service

**API Name & Overview**
Create Laundry Service. Adds a new service (e.g., Wash & Fold, Dry Cleaning) for a specific partner.

**HTTP Method**
`POST`

**Endpoint URL**
`/api/catalog/partners/{partnerId}/services`

**Full URL**
`https://api.washgo.in/api/catalog/partners/{partnerId}/services`

**Authentication**
Not Required

**Roles**
TODO - not found in code

**Headers**
| Header | Type | Required | Description |
|---|---|---|---|
| Content-Type | string | Yes | `application/json` |

**Path Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| `partnerId` | Long | Yes | ID of the laundry partner |

**Query Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Request Body**
```json
{
  "serviceName": "Wash & Fold",
  "description": "Standard wash, dry, and fold service per kg"
}
```

**Success Response**
HTTP Status: `201 Created`
```json
{
  "id": 101,
  "serviceName": "Wash & Fold",
  "description": "Standard wash, dry, and fold service per kg",
  "active": true
}
```

**Error Responses**
- `500 Internal Server Error`: `RuntimeException` thrown if the `partnerId` does not exist.

**Validation Rules**
TODO - not found in code

**Database Tables**
- **Entity**: `LaundryService`
- **Table**: `laundry_services`
- **Operations**: Insert new service with a foreign key reference to `laundry_partners`.

**Service Flow**
`LaundryServiceController` → `LaundryServiceServiceImpl.createService` → Fetches Partner → `LaundryServiceRepository.save` → Database

**Events**
TODO - not found in code

**cURL example**
```bash
curl -X POST https://api.washgo.in/api/catalog/partners/1/services \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "Wash & Fold",
    "description": "Standard wash, dry, and fold service per kg"
  }'
```

**JavaScript Fetch example**
```javascript
fetch("https://api.washgo.in/api/catalog/partners/1/services", {
  method: "POST",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify({
    serviceName: "Wash & Fold",
    description: "Standard wash, dry, and fold service per kg"
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

**Axios example**
```javascript
axios.post("https://api.washgo.in/api/catalog/partners/1/services", {
  serviceName: "Wash & Fold",
  description: "Standard wash, dry, and fold service per kg"
})
.then(response => console.log(response.data));
```

**Java example**
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/catalog/partners/1/services"))
  .header("Content-Type", "application/json")
  .POST(HttpRequest.BodyPublishers.ofString("{\"serviceName\":\"Wash & Fold\",\"description\":\"Standard wash, dry, and fold service per kg\"}"))
  .build();

HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
```

**Notes**
Fails if the partner is not found.

---

## 5. Get Partner Services

**API Name & Overview**
Get Partner Services. Retrieves all active and inactive services offered by a specific partner.

**HTTP Method**
`GET`

**Endpoint URL**
`/api/catalog/partners/{partnerId}/services`

**Full URL**
`https://api.washgo.in/api/catalog/partners/{partnerId}/services`

**Authentication**
Not Required

**Roles**
TODO - not found in code

**Headers**
| Header | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Path Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| `partnerId` | Long | Yes | ID of the laundry partner |

**Query Parameters**
| Parameter | Type | Required | Description |
|---|---|---|---|
| None | | | |

**Request Body**
None

**Success Response**
HTTP Status: `200 OK`
```json
[
  {
    "id": 101,
    "serviceName": "Wash & Fold",
    "description": "Standard wash, dry, and fold service per kg",
    "active": true
  },
  {
    "id": 102,
    "serviceName": "Dry Cleaning",
    "description": "Premium dry cleaning for delicate fabrics",
    "active": true
  }
]
```

**Error Responses**
- `500 Internal Server Error`: `RuntimeException` thrown if the `partnerId` does not exist.

**Validation Rules**
TODO - not found in code

**Database Tables**
- **Entity**: `LaundryService`
- **Table**: `laundry_services`
- **Operations**: Select records filtered by `partner_id`.

**Service Flow**
`LaundryServiceController` → `LaundryServiceServiceImpl.getPartnerServices` → `LaundryServiceRepository.findByPartner` → Database

**Events**
TODO - not found in code

**cURL example**
```bash
curl -X GET https://api.washgo.in/api/catalog/partners/1/services
```

**JavaScript Fetch example**
```javascript
fetch("https://api.washgo.in/api/catalog/partners/1/services")
  .then(response => response.json())
  .then(data => console.log(data));
```

**Axios example**
```javascript
axios.get("https://api.washgo.in/api/catalog/partners/1/services")
  .then(response => console.log(response.data));
```

**Java example**
```java
HttpRequest request = HttpRequest.newBuilder()
  .uri(URI.create("https://api.washgo.in/api/catalog/partners/1/services"))
  .GET()
  .build();

HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
```

**Notes**
Services are loaded by referencing the partner entity directly.

---

## Endpoint Summary

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/catalog/partners` | Create a new laundry partner |
| `GET` | `/api/catalog/partners` | Get a list of all laundry partners |
| `GET` | `/api/catalog/partners/{id}` | Get a specific laundry partner by ID |
| `POST` | `/api/catalog/partners/{partnerId}/services` | Create a new laundry service for a partner |
| `GET` | `/api/catalog/partners/{partnerId}/services` | Get all services for a specific partner |
