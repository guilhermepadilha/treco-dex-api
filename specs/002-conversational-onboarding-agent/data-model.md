# Data Model: Conversational Onboarding Agent

## Ephemeral Entities (Redis)

### OnboardingSession

Tracks the active conversational onboarding context.

```json
{
  "userId": "String (UUID)",
  "step": "Enum (AWAITING_PHOTO, AWAITING_PHOTO_CONFIRMATION, AWAITING_HABITAT)",
  "objectName": "String",
  "objectPhotoUrl": "String",
  "habitatName": "String",
  "suggestedReasoning": "String",
  "chatHistory": [
    {
      "sender": "Enum (USER, AI)",
      "message": "String",
      "timestamp": "Long"
    }
  ]
}
```

---

## AI Parser Output Contract

### OnboardingIntent

The structured model output extracted by the LLM parser.

*   `confirmed`: `Boolean` — Indicates whether the user accepted the object name.
*   `objectName`: `String` — The corrected or confirmed name of the object.
*   `habitatName`: `String` — The extracted name of the physical location/habitat.
*   `reply`: `String` — A friendly, context-appropriate reply to the user.
