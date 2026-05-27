#!/bin/bash

echo "=== Starting Test Script ==="

# 1. Try to sign up
echo "Attempting Sign Up..."
SIGNUP_RES=$(curl -s -X POST http://localhost:8082/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"tester","email":"tester@example.com","password":"password123"}')

echo "Sign Up Response: $SIGNUP_RES"

TOKEN=$(echo "$SIGNUP_RES" | grep -o '"token":"[^"]*' | grep -o '[^"]*$')

if [ -z "$TOKEN" ]; then
  echo "Sign up did not yield a token (maybe user already exists). Attempting Log In..."
  LOGIN_RES=$(curl -s -X POST http://localhost:8082/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"tester","password":"password123"}')
  echo "Log In Response: $LOGIN_RES"
  TOKEN=$(echo "$LOGIN_RES" | grep -o '"token":"[^"]*' | grep -o '[^"]*$')
fi

if [ -z "$TOKEN" ]; then
  echo "ERROR: Failed to obtain JWT token."
  exit 1
fi

echo "Successfully obtained Token: $TOKEN"

# 2. Create a dummy image file
echo "creating dummy image..."
echo "GIF89a\x01\x00\x01\x00\x80\x00\x00\xff\xff\xff\x00\x00\x00!\xf9\x04\x01\x00\x00\x00\x00,\x00\x00\x00\x00\x01\x00\x01\x00\x00\x02\x02D\x01\x00;" > dummy_image.jpg

# 3. Call visual-search
echo "Sending POST /api/objects/visual-search with Multipart File..."
RESPONSE=$(curl -s -X POST http://localhost:8082/api/objects/visual-search \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@dummy_image.jpg")

echo "======================================"
echo "Visual Search JSON Response:"
echo "$RESPONSE"
echo "======================================"

# Clean up
rm -f dummy_image.jpg
