# URL Shortener

A simple URL shortener.

## Installation and running

To build the application simply use (make sure that JAVA_HOME is correctly set up to Java 17):
```bash
export JAVA_HOME=<path-to-java>
./mvnw spring-boot:run
```

## API Endpoints

### 1. Register an account

**Endpoint:** POST /account

**Description:**
Register a new account.

**Request:**
```bash
curl --request POST \
--url http://localhost:8080/account \
--header 'Content-Type: application/json' \
--data '{
"accountId": "test"
}'
```

**Response:**
```bash
{
	"success": true,
	"description": "User has been successfully created.",
	"password": "LrMcY2HV"
}
```

To get an auth header encoded:
```bash
echo -n 'test:LrMcY2HV' | base64
```

### 2. Register URL

**Endpoint:** POST /register

**Description:**
Register a new URL.

**Request:**
```bash
curl --request POST \
  --url http://localhost:8080/register \
  --header 'Authorization: Basic dGVzdDpMck1jWTJIVg==' \
  --header 'Content-Type: application/json' \
  --data '{
	"url": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
	"redirectType": 301
}'
```

**Response:**
```bash
{
	"shortUrl": "http://localhost:8080/EnXe7lpjP2"
}
```

### 3. Redirect URL

**Endpoint:** GET /{shortcode}

**Description:**
Redirect shortened URL to the original one.

**Request:**
```bash
curl --request GET \
  --url http://localhost:8080/EnXe7lpjP2
```

### 4. Statistics

**Endpoint:** GET /statistic/{accountId}

**Description:**
Get accountId's statistics.

**Request:**
```bash
curl --request GET \
  --url http://localhost:8080/statistic/test \
  --header 'Authorization: Basic dGVzdDpMck1jWTJIVg=='
```

**Response:**
```bash
{
	"https://www.youtube.com/watch?v=dQw4w9WgXcQ": 5
}
```