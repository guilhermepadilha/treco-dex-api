# Quickstart: Conversational Onboarding Agent

## Testing Endpoint via cURL

### 1. Iniciar o Onboarding (Enviando a Foto)
```bash
curl -X POST http://localhost:8080/api/objects/visual-search \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@photo.jpg"
```
*Guarde o ID da sessão retornado na resposta.*

### 2. Enviar Mensagem Conversacional
```bash
curl -X POST http://localhost:8080/api/objects/chat-onboarding \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "sessionId": "ba62182f-377c-4c2b-950f-3f15ddcb532c",
    "message": "Sim é um mouse e ele vive na mesa do escritório"
  }'
```

### 3. Verificar o Objeto no Catálogo
```bash
curl -X GET http://localhost:8080/api/objects \
  -H "Authorization: Bearer <TOKEN>"
```
