# Food Delivery System - API Request Reference

Base URL: `http://localhost:8080`

---

## 1. Authentication

### 1.1 Register User

```
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "phoneNo": "1234567890",
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe",
  "address": "123 Main Street, City, State",
  "role": "USER"
}
```

### 1.2 Register Restaurant Owner

```
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "phoneNo": "1234567891",
  "email": "restaurant@example.com",
  "password": "password123",
  "name": "Restaurant Owner",
  "address": "456 Restaurant Ave",
  "role": "RESTAURANT"
}
```

### 1.3 Register Delivery Partner

```
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "phoneNo": "1234567892",
  "email": "driver@example.com",
  "password": "password123",
  "name": "Driver Name",
  "address": "789 Driver Lane",
  "role": "DELIVERY_PARTNER"
}
```

### 1.4 Register Admin

```
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "phoneNo": "1234567893",
  "email": "admin@example.com",
  "password": "password123",
  "name": "Admin User",
  "address": "Admin Building",
  "role": "ADMIN"
}
```

### 1.5 Login (with email)

```
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "identifier": "user@example.com",
  "password": "password123"
}
```

### 1.6 Login (with phone)

```
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "identifier": "1234567890",
  "password": "password123"
}
```

---

## 2. Restaurants

### 2.1 Create Restaurant

```
POST /api/restaurants
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT or ADMIN role

```json
{
  "restaurantName": "Pizza Palace",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "latitude": 40.7128,
  "longitude": -74.006,
  "openingTime": "09:00:00",
  "closingTime": "22:00:00",
  "imgUrl": "https://example.com/pizza.jpg",
  "isOpen": true
}
```

### 2.2 Get Restaurant by ID

```
GET /api/restaurants/{id}
```

Public endpoint - No auth required

### 2.3 Update Restaurant

```
PUT /api/restaurants/{id}
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT or ADMIN role

```json
{
  "restaurantName": "Pizza Palace Updated",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "latitude": 40.7128,
  "longitude": -74.006,
  "openingTime": "09:00:00",
  "closingTime": "23:00:00",
  "imgUrl": "https://example.com/pizza-new.jpg",
  "isOpen": true
}
```

### 2.4 Get Restaurants (Search & Filter)

```
GET /api/restaurants?city=New York&state=NY&isOpen=true&page=0&size=10
```

Public endpoint - No auth required

### 2.5 Get My Restaurant

```
GET /api/restaurants/my-restaurant
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT role

---

## 3. Food Items

### 3.1 Create Food Item

```
POST /api/food-items?restaurantId={restaurantId}
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT or ADMIN role

```json
{
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato and mozzarella",
  "price": 12.99,
  "imgUrl": "https://example.com/margherita.jpg",
  "isAvailable": true
}
```

### 3.2 Get Food Item by ID

```
GET /api/food-items/{id}
```

Public endpoint - No auth required

### 3.3 Update Food Item

```
PUT /api/food-items/{id}
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT or ADMIN role

```json
{
  "name": "Margherita Pizza Updated",
  "description": "Classic pizza with tomato and mozzarella - now with extra cheese!",
  "price": 14.99,
  "imgUrl": "https://example.com/margherita-new.jpg",
  "isAvailable": true
}
```

### 3.4 Delete Food Item

```
DELETE /api/food-items/{id}
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT or ADMIN role (Soft delete)

### 3.5 Get Food Items by Restaurant

```
GET /api/food-items/restaurants/{restaurantId}?availableOnly=false
```

Public endpoint - No auth required

---

## 4. Orders

### 4.1 Create Order

```
POST /api/orders
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: USER or ADMIN role

```json
{
  "restaurantId": "{{restaurantId}}",
  "items": [
    {
      "foodItemId": "{{foodItemId1}}",
      "quantity": 2
    },
    {
      "foodItemId": "{{foodItemId2}}",
      "quantity": 1
    }
  ],
  "address": "123 Main Street, New York, NY 10001",
  "latitude": 40.7128,
  "longitude": -74.006
}
```

### 4.2 Get Order by ID

```
GET /api/orders/{id}
Authorization: Bearer {{accessToken}}
```

Requires: USER, RESTAURANT, DELIVERY_PARTNER, or ADMIN role

### 4.3 Get My Orders

```
GET /api/orders
Authorization: Bearer {{accessToken}}
```

Requires: USER, RESTAURANT, DELIVERY_PARTNER, or ADMIN role

### 4.4 Update Order Status

```
PUT /api/orders/{id}/status
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT, DELIVERY_PARTNER, or ADMIN role

Valid statuses: `PENDING`, `CONFIRMED`, `PREPARING`, `READY`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`

```json
{
  "orderStatus": "CONFIRMED"
}
```

### 4.5 Assign Driver to Order

```
PUT /api/orders/{id}/assign-driver?driverId={driverId}
Authorization: Bearer {{accessToken}}
```

Requires: ADMIN role

### 4.6 Get Order History

```
GET /api/orders/history
Authorization: Bearer {{accessToken}}
```

Requires: USER, RESTAURANT, DELIVERY_PARTNER, or ADMIN role

---

## 5. Drivers

### 5.1 Register Driver

```
POST /api/drivers
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: DELIVERY_PARTNER or ADMIN role

```json
{
  "licenseNumber": "DL123456789",
  "vehicleNumber": "ABC-1234"
}
```

### 5.2 Get Driver by ID

```
GET /api/drivers/{id}
Authorization: Bearer {{accessToken}}
```

Requires: DELIVERY_PARTNER or ADMIN role

### 5.3 Update Driver Location

```
PUT /api/drivers/{id}/location
Content-Type: application/json
Authorization: Bearer {{accessToken}}
```

Requires: DELIVERY_PARTNER or ADMIN role

```json
{
  "latitude": 40.7128,
  "longitude": -74.006
}
```

### 5.4 Set Driver Availability

```
PUT /api/drivers/{id}/availability?available=true
Authorization: Bearer {{accessToken}}
```

Requires: DELIVERY_PARTNER or ADMIN role

### 5.5 Get Available Drivers

```
GET /api/drivers/available
Authorization: Bearer {{accessToken}}
```

Requires: RESTAURANT or ADMIN role

---

## 6. Admin - Cache Management

### 6.1 Delete Cache Key

```
DELETE /api/admin/cache/{key}
Authorization: Bearer {{accessToken}}
```

Requires: ADMIN role

### 6.2 Warm Up Cache

```
POST /api/admin/cache/warm-up
Authorization: Bearer {{accessToken}}
```

Requires: ADMIN role

### 6.3 Evict Expired Cache Entries

```
POST /api/admin/cache/evict
Authorization: Bearer {{accessToken}}
```

Requires: ADMIN role

### 6.4 Get Cache Statistics

```
GET /api/admin/cache/statistics
Authorization: Bearer {{accessToken}}
```

Requires: ADMIN role

---

## 7. Health & Monitoring

### 7.1 Health Check

```
GET /actuator/health
```

Public endpoint - No auth required

### 7.2 Metrics

```
GET /actuator/metrics
```

Public endpoint - No auth required

### 7.3 Prometheus Metrics

```
GET /actuator/prometheus
```

Public endpoint - No auth required

---

## Order Status Flow

```
PENDING → CONFIRMED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED
                                                           ↘ CANCELLED
```

---

## Roles

| Role               | Description                                               |
| ------------------ | --------------------------------------------------------- |
| `USER`             | Regular customer who can place orders                     |
| `RESTAURANT`       | Restaurant owner who can manage restaurant and food items |
| `DELIVERY_PARTNER` | Driver who can deliver orders                             |
| `ADMIN`            | System administrator with full access                     |

---

## Sample cURL Commands

### Login and save token

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"user@example.com","password":"password123"}' \
  | jq -r '.accessToken')
echo $TOKEN
```

### Create Restaurant with token

```bash
curl -X POST http://localhost:8080/api/restaurants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "restaurantName": "Pizza Palace",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "openingTime": "09:00:00",
    "closingTime": "22:00:00",
    "isOpen": true
  }'
```

### Create Order with token

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "restaurantId": "YOUR_RESTAURANT_ID",
    "items": [
      {"foodItemId": "YOUR_FOOD_ITEM_ID", "quantity": 2}
    ],
    "address": "123 Main Street",
    "latitude": 40.7128,
    "longitude": -74.0060
  }'
```
