#!/usr/bin/env bash
# -------------------------------------------------
# TrecoDex API – startup script
# -------------------------------------------------
# Prerequisites:
#   • SDKMAN installed (https://sdkman.io)
#   • Java 21 available via SDKMAN
#   • Docker Compose installed
# -------------------------------------------------

set -euo pipefail   # Fail fast on errors / unset vars

# Check if test mode is requested via parameter (--test, -t, or test)
RUN_TESTS=false
for arg in "$@"; do
    if [[ "$arg" == "--test" || "$arg" == "-t" || "$arg" == "test" ]]; then
        RUN_TESTS=true
    fi
done

# 1️⃣ Select JDK 21 via SDKMAN
export SDKMAN_DIR="$HOME/.sdkman"
if [[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]]; then
    echo "🔧 Loading SDKMAN..."
    set +u # Disable nounset temporarily as sdkman script checks unbound variables
    source "$SDKMAN_DIR/bin/sdkman-init.sh"
    set -u # Re-enable nounset
    echo "🔧 Selecting Java 21.0.2-tem..."
    sdk use java 21.0.2-tem
else
    echo "⚠️ SDKMAN not found at $SDKMAN_DIR. Checking system Java version..."
    if command -v java >/dev/null 2>&1; then
        JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d'"' -f 2 | cut -d'.' -f 1)
        if [ "$JAVA_VER" = "21" ]; then
            echo "✅ System Java is already version 21. Continuing..."
        else
            echo "❌ System Java version is $JAVA_VER (not 21). Please install JDK 21."
            exit 1
        fi
    else
        echo "❌ Java is not installed or not on PATH."
        exit 1
    fi
fi

# 2️⃣ Export environment variables from .env (ignore commented lines)
if [[ -f .env ]]; then
    echo "🔐 Exporting variables from .env..."
    # shellcheck disable=SC2046
    export $(grep -v '^#' .env | xargs)
else
    echo "⚠️ .env file not found at project root. Continue if vars are already set."
fi

# 3️⃣ Build/Test and Container Startup
if [ "$RUN_TESTS" = true ]; then
    echo "⚙️ Running clean build and tests..."
    ./gradlew clean build

    echo "🐳 Starting Docker containers (Postgres, Redis, Kafka)..."
    docker-compose -f docker/docker-compose.yml up -d

    echo "⚙️ Starting Spring Boot..."
    ./gradlew bootRun
else
    echo "🐳 Starting Docker containers (Postgres, Redis, Kafka)..."
    docker-compose -f docker/docker-compose.yml up -d

    echo "⚙️ Compiling and starting Spring Boot..."
    ./gradlew clean bootRun
fi

# 5️⃣ Final message
echo "🎉 API is running at http://localhost:8080/api"
