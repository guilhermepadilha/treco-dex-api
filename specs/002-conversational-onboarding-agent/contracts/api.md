# API Contract: Conversational Onboarding

## Endpoint: `POST /api/objects/chat-onboarding`

Processes a user's conversational message during onboarding to confirm the object or register a habitat.

### Request Headers
*   `Content-Type`: `application/json`
*   `Authorization`: `Bearer <JWT_TOKEN>`

### Request Body
```json
{
  "sessionId": "ba62182f-377c-4c2b-950f-3f15ddcb532c",
  "message": "Sim é um mouse e ele vive na mesa do escritório"
}
```

### Response Headers
*   `Content-Type`: `application/json`

### Response Body (200 OK)
```json
{
  "sessionId": "ba62182f-377c-4c2b-950f-3f15ddcb532c",
  "step": "AWAITING_HABITAT",
  "objectName": "mouse",
  "habitatName": "mesa do escritório",
  "reply": "Entendido! O seu mouse será guardado na mesa do escritório.",
  "completed": true
}
```

### Response Body (400 Bad Request / 404 Not Found)
```json
{
  "error": "Sessão expirada ou não encontrada. Por favor escaneie o objeto novamente."
}
```
