#!/bin/bash

BASE_URL="http://localhost:8080"
TIMESTAMP=$(date +%s)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================"
echo "   FOOD DELIVERY SYSTEM API DRY RUN    "
echo "========================================"
echo ""

# Track results
declare -A RESULTS

test_endpoint() {
    local name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    local token="$5"
    
    echo -n "Testing: $name... "
    
    local headers="-H 'Content-Type: application/json'"
    if [ -n "$token" ]; then
        headers="$headers -H 'Authorization: Bearer $token'"
    fi
    
    local response
    if [ "$method" == "GET" ]; then
        if [ -n "$token" ]; then
            response=$(curl -s -w "\n%{http_code}" "$url" -H "Authorization: Bearer $token")
        else
            response=$(curl -s -w "\n%{http_code}" "$url")
        fi
    elif [ "$method" == "DELETE" ]; then
        response=$(curl -s -w "\n%{http_code}" -X DELETE "$url" -H "Authorization: Bearer $token")
    else
        if [ -n "$token" ]; then
            response=$(curl -s -w "\n%{http_code}" -X "$method" "$url" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" "$url" -H "Content-Type: application/json" -d "$data")
        fi
    fi
    
    local http_code=$(echo "$response" | tail -1)
    local body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        RESULTS["$name"]="PASS"
    elif [ "$http_code" -eq 409 ]; then
        echo -e "${YELLOW}○ EXISTS${NC} (HTTP $http_code - Resource already exists)"
        RESULTS["$name"]="EXISTS"
    elif [ "$http_code" -eq 401 ] || [ "$http_code" -eq 403 ]; then
        echo -e "${RED}✗ FAIL${NC} (HTTP $http_code - Auth issue)"
        RESULTS["$name"]="FAIL-AUTH"
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $http_code)"
        RESULTS["$name"]="FAIL"
    fi
    
    echo "$body"
    echo "---"
}

echo "=== 1. HEALTH CHECK ==="
curl -s "$BASE_URL/actuator/health" | jq -c .
echo ""

echo "=== 2. AUTHENTICATION ==="

# Register USER
echo "Registering USER..."
USER_RESP=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"phoneNo\":\"111$TIMESTAMP\",\"email\":\"user$TIMESTAMP@test.com\",\"password\":\"password123\",\"name\":\"Test User\",\"address\":\"123 Main St\",\"role\":\"USER\"}")
echo "$USER_RESP" | jq -c '{"email":.email,"role":.role,"status": (if .accessToken then "SUCCESS" else "FAILED" end)}'
USER_TOKEN=$(echo "$USER_RESP" | jq -r '.accessToken // empty')
USER_ID=$(echo "$USER_RESP" | jq -r '.userId // empty')

# Register RESTAURANT owner
echo "Registering RESTAURANT owner..."
REST_RESP=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"phoneNo\":\"222$TIMESTAMP\",\"email\":\"resto$TIMESTAMP@test.com\",\"password\":\"password123\",\"name\":\"Restaurant Owner\",\"address\":\"456 Food St\",\"role\":\"RESTAURANT\"}")
echo "$REST_RESP" | jq -c '{"email":.email,"role":.role,"status": (if .accessToken then "SUCCESS" else "FAILED" end)}'
REST_TOKEN=$(echo "$REST_RESP" | jq -r '.accessToken // empty')
REST_USER_ID=$(echo "$REST_RESP" | jq -r '.userId // empty')

# Register DELIVERY_PARTNER
echo "Registering DELIVERY_PARTNER..."
DRIVER_RESP=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"phoneNo\":\"333$TIMESTAMP\",\"email\":\"driver$TIMESTAMP@test.com\",\"password\":\"password123\",\"name\":\"Driver Guy\",\"address\":\"789 Delivery Ave\",\"role\":\"DELIVERY_PARTNER\"}")
echo "$DRIVER_RESP" | jq -c '{"email":.email,"role":.role,"status": (if .accessToken then "SUCCESS" else "FAILED" end)}'
DRIVER_TOKEN=$(echo "$DRIVER_RESP" | jq -r '.accessToken // empty')
DRIVER_USER_ID=$(echo "$DRIVER_RESP" | jq -r '.userId // empty')

# Register ADMIN
echo "Registering ADMIN..."
ADMIN_RESP=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"phoneNo\":\"444$TIMESTAMP\",\"email\":\"admin$TIMESTAMP@test.com\",\"password\":\"password123\",\"name\":\"Admin User\",\"address\":\"Admin Building\",\"role\":\"ADMIN\"}")
echo "$ADMIN_RESP" | jq -c '{"email":.email,"role":.role,"status": (if .accessToken then "SUCCESS" else "FAILED" end)}'
ADMIN_TOKEN=$(echo "$ADMIN_RESP" | jq -r '.accessToken // empty')
ADMIN_USER_ID=$(echo "$ADMIN_RESP" | jq -r '.userId // empty')

echo ""
echo "=== 3. LOGIN TEST ==="
LOGIN_RESP=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"identifier\":\"user$TIMESTAMP@test.com\",\"password\":\"password123\"}")
echo "$LOGIN_RESP" | jq -c '{"email":.email,"status": (if .accessToken then "SUCCESS" else "FAILED" end)}'

echo ""
echo "=== 4. RESTAURANT ENDPOINTS ==="

# Create Restaurant
echo "Creating Restaurant..."
RESTO_CREATE=$(curl -s -X POST "$BASE_URL/api/restaurants" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $REST_TOKEN" \
    -d '{
        "restaurantName": "Test Pizza Palace",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "latitude": 40.7128,
        "longitude": -74.0060,
        "openingTime": "09:00:00",
        "closingTime": "22:00:00",
        "imgUrl": "https://example.com/pizza.jpg",
        "isOpen": true
    }')
echo "$RESTO_CREATE" | jq -c '{"id":.id,"name":.restaurantName,"status": (if .id then "SUCCESS" else "FAILED" end)}'
RESTAURANT_ID=$(echo "$RESTO_CREATE" | jq -r '.id // empty')

# Get Restaurant by ID
echo "Getting Restaurant by ID..."
curl -s "$BASE_URL/api/restaurants/$RESTAURANT_ID" -H "Authorization: Bearer $REST_TOKEN" | jq -c '{"id":.id,"name":.restaurantName}'

# Search Restaurants
echo "Searching Restaurants..."
curl -s "$BASE_URL/api/restaurants?city=New%20York" -H "Authorization: Bearer $REST_TOKEN" | jq -c 'if type=="array" then {"count": length} else {"status":"error"} end'

# Get My Restaurant
echo "Getting My Restaurant..."
curl -s "$BASE_URL/api/restaurants/my-restaurant" -H "Authorization: Bearer $REST_TOKEN" | jq -c '{"id":.id,"name":.restaurantName}'

echo ""
echo "=== 5. FOOD ITEM ENDPOINTS ==="

# Create Food Item
echo "Creating Food Item..."
FOOD_CREATE=$(curl -s -X POST "$BASE_URL/api/food-items?restaurantId=$RESTAURANT_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $REST_TOKEN" \
    -d '{
        "name": "Margherita Pizza",
        "description": "Classic tomato and mozzarella",
        "price": 12.99,
        "imgUrl": "https://example.com/margherita.jpg",
        "isAvailable": true
    }')
echo "$FOOD_CREATE" | jq -c '{"id":.id,"name":.name,"price":.price,"status": (if .id then "SUCCESS" else "FAILED" end)}'
FOOD_ITEM_ID=$(echo "$FOOD_CREATE" | jq -r '.id // empty')

# Create another Food Item
echo "Creating Second Food Item..."
FOOD_CREATE2=$(curl -s -X POST "$BASE_URL/api/food-items?restaurantId=$RESTAURANT_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $REST_TOKEN" \
    -d '{
        "name": "Pepperoni Pizza",
        "description": "Pizza with pepperoni",
        "price": 14.99,
        "imgUrl": "https://example.com/pepperoni.jpg",
        "isAvailable": true
    }')
echo "$FOOD_CREATE2" | jq -c '{"id":.id,"name":.name,"price":.price}'
FOOD_ITEM_ID_2=$(echo "$FOOD_CREATE2" | jq -r '.id // empty')

# Get Food Item by ID
echo "Getting Food Item by ID..."
curl -s "$BASE_URL/api/food-items/$FOOD_ITEM_ID" -H "Authorization: Bearer $REST_TOKEN" | jq -c '{"id":.id,"name":.name}'

# Get Food Items by Restaurant
echo "Getting Food Items by Restaurant..."
curl -s "$BASE_URL/api/food-items/restaurants/$RESTAURANT_ID" -H "Authorization: Bearer $REST_TOKEN" | jq -c 'if type=="array" then {"count": length} else . end'

echo ""
echo "=== 6. DRIVER ENDPOINTS ==="

# Register Driver Profile
echo "Registering Driver Profile..."
DRIVER_PROFILE=$(curl -s -X POST "$BASE_URL/api/drivers" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $DRIVER_TOKEN" \
    -d '{
        "licenseNumber": "DL123456789",
        "vehicleNumber": "ABC-1234"
    }')
echo "$DRIVER_PROFILE" | jq -c '{"id":.id,"license":.licenseNumber,"status": (if .id then "SUCCESS" else .message // "FAILED" end)}'
DRIVER_ID=$(echo "$DRIVER_PROFILE" | jq -r '.id // empty')

# Get Driver by ID
echo "Getting Driver by ID..."
curl -s "$BASE_URL/api/drivers/$DRIVER_ID" -H "Authorization: Bearer $DRIVER_TOKEN" | jq -c '{"id":.id,"license":.licenseNumber}'

# Update Driver Location
echo "Updating Driver Location..."
curl -s -X PUT "$BASE_URL/api/drivers/$DRIVER_ID/location" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $DRIVER_TOKEN" \
    -d '{"latitude": 40.7128, "longitude": -74.0060}' | jq -c '.'

# Set Driver Availability
echo "Setting Driver Availability..."
curl -s -X PUT "$BASE_URL/api/drivers/$DRIVER_ID/availability?available=true" \
    -H "Authorization: Bearer $DRIVER_TOKEN" | jq -c '.'

# Get Available Drivers
echo "Getting Available Drivers..."
curl -s "$BASE_URL/api/drivers/available" -H "Authorization: Bearer $ADMIN_TOKEN" | jq -c 'if type=="array" then {"count": length} else . end'

echo ""
echo "=== 7. ORDER ENDPOINTS ==="

# Create Order
echo "Creating Order..."
ORDER_CREATE=$(curl -s -X POST "$BASE_URL/api/orders" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER_TOKEN" \
    -d "{
        \"restaurantId\": \"$RESTAURANT_ID\",
        \"items\": [
            {\"foodItemId\": \"$FOOD_ITEM_ID\", \"quantity\": 2},
            {\"foodItemId\": \"$FOOD_ITEM_ID_2\", \"quantity\": 1}
        ],
        \"address\": \"123 Delivery St, New York, NY 10001\",
        \"latitude\": 40.7128,
        \"longitude\": -74.0060
    }")
echo "$ORDER_CREATE" | jq -c '{"id":.id,"status":.orderStatus,"total":.totalAmount,"result": (if .id then "SUCCESS" else .message // "FAILED" end)}'
ORDER_ID=$(echo "$ORDER_CREATE" | jq -r '.id // empty')

# Get Order by ID
echo "Getting Order by ID..."
curl -s "$BASE_URL/api/orders/$ORDER_ID" -H "Authorization: Bearer $USER_TOKEN" | jq -c '{"id":.id,"status":.orderStatus}'

# Get My Orders
echo "Getting My Orders..."
curl -s "$BASE_URL/api/orders" -H "Authorization: Bearer $USER_TOKEN" | jq -c 'if type=="array" then {"count": length} else . end'

# Update Order Status (RESTAURANT - CONFIRMED)
echo "Restaurant confirming order..."
curl -s -X PUT "$BASE_URL/api/orders/$ORDER_ID/status" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $REST_TOKEN" \
    -d '{"orderStatus": "CONFIRMED"}' | jq -c '{"id":.id,"status":.orderStatus}'

# Update Order Status (RESTAURANT - PREPARING)
echo "Restaurant preparing order..."
curl -s -X PUT "$BASE_URL/api/orders/$ORDER_ID/status" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $REST_TOKEN" \
    -d '{"orderStatus": "PREPARING"}' | jq -c '{"id":.id,"status":.orderStatus}'

# Update Order Status (RESTAURANT - READY)
echo "Restaurant marking order ready..."
curl -s -X PUT "$BASE_URL/api/orders/$ORDER_ID/status" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $REST_TOKEN" \
    -d '{"orderStatus": "READY"}' | jq -c '{"id":.id,"status":.orderStatus}'

# Assign Driver (ADMIN)
echo "Assigning Driver to Order..."
curl -s -X PUT "$BASE_URL/api/orders/$ORDER_ID/assign-driver?driverId=$DRIVER_ID" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -c '{"id":.id,"driver":.driverId,"status":.orderStatus}'

# Update Order Status (DRIVER - OUT_FOR_DELIVERY)
echo "Driver picking up order..."
curl -s -X PUT "$BASE_URL/api/orders/$ORDER_ID/status" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $DRIVER_TOKEN" \
    -d '{"orderStatus": "OUT_FOR_DELIVERY"}' | jq -c '{"id":.id,"status":.orderStatus}'

# Update Order Status (DRIVER - DELIVERED)
echo "Driver delivering order..."
curl -s -X PUT "$BASE_URL/api/orders/$ORDER_ID/status" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $DRIVER_TOKEN" \
    -d '{"orderStatus": "DELIVERED"}' | jq -c '{"id":.id,"status":.orderStatus}'

# Get Order History
echo "Getting Order History..."
curl -s "$BASE_URL/api/orders/history" -H "Authorization: Bearer $USER_TOKEN" | jq -c 'if type=="array" then {"count": length} else . end'

echo ""
echo "=== 8. ADMIN CACHE ENDPOINTS ==="

# Get Cache Statistics
echo "Getting Cache Statistics..."
curl -s "$BASE_URL/api/admin/cache/statistics" -H "Authorization: Bearer $ADMIN_TOKEN" | jq -c '.'

# Warm Up Cache
echo "Warming Up Cache..."
curl -s -X POST "$BASE_URL/api/admin/cache/warm-up" -H "Authorization: Bearer $ADMIN_TOKEN" | jq -c '.'

# Evict Expired Cache
echo "Evicting Expired Cache..."
curl -s -X POST "$BASE_URL/api/admin/cache/evict" -H "Authorization: Bearer $ADMIN_TOKEN" | jq -c '.'

echo ""
echo "=== 9. MONITORING ENDPOINTS ==="

# Metrics
echo "Getting Metrics..."
curl -s "$BASE_URL/actuator/metrics" | jq -c '{"names_count": (.names | length)}'

# Prometheus Metrics
echo "Getting Prometheus Metrics (first 5 lines)..."
curl -s "$BASE_URL/actuator/prometheus" | head -5

echo ""
echo "========================================"
echo "            TEST SUMMARY               "
echo "========================================"
echo "USER_TOKEN: ${USER_TOKEN:0:30}..."
echo "REST_TOKEN: ${REST_TOKEN:0:30}..."
echo "DRIVER_TOKEN: ${DRIVER_TOKEN:0:30}..."
echo "ADMIN_TOKEN: ${ADMIN_TOKEN:0:30}..."
echo "RESTAURANT_ID: $RESTAURANT_ID"
echo "FOOD_ITEM_ID: $FOOD_ITEM_ID"
echo "DRIVER_ID: $DRIVER_ID"
echo "ORDER_ID: $ORDER_ID"
echo "========================================"
