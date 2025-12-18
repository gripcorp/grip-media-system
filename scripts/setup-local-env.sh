#!/bin/bash

###############################################################################
# grip-media-system Local Environment Setup Script
#
# 이 스크립트는 로컬 개발 환경을 자동으로 설정합니다.
###############################################################################

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="$PROJECT_ROOT/.env"
ENV_EXAMPLE="$PROJECT_ROOT/.env.example"

echo "================================================"
echo "grip-media-system Local Environment Setup"
echo "================================================"
echo ""

# 1. .env 파일 체크
if [ -f "$ENV_FILE" ]; then
    echo "✅ .env 파일이 이미 존재합니다: $ENV_FILE"
    read -p "덮어쓰시겠습니까? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "기존 .env 파일을 유지합니다."
        exit 0
    fi
fi

# 2. .env.example 복사
if [ ! -f "$ENV_EXAMPLE" ]; then
    echo "❌ .env.example 파일을 찾을 수 없습니다!"
    exit 1
fi

echo "📋 .env.example을 .env로 복사합니다..."
cp "$ENV_EXAMPLE" "$ENV_FILE"
echo "✅ .env 파일 생성 완료"
echo ""

# 3. 사용자에게 값 입력 요청
echo "================================================"
echo "환경 변수 값을 입력하세요"
echo "================================================"
echo ""
echo "💡 팁: 실제 값은 팀 내부 문서(Notion/Confluence) 또는"
echo "   DevOps 팀에게 문의하세요"
echo ""

read -p "DB Username [grip]: " db_user
db_user=${db_user:-grip}

read -sp "DB Password: " db_password
echo ""

read -p "Google Cloud API Key (선택사항): " google_api_key
read -p "Facebook Secret Key (선택사항): " facebook_secret

# 4. .env 파일에 실제 값 작성
cat > "$ENV_FILE" << EOF
# grip-media-system Local Environment Variables
# 자동 생성됨: $(date)

# Database Configuration
LOCAL_DB_USERNAME=$db_user
LOCAL_DB_PASSWORD=$db_password

# Google Cloud Configuration
GOOGLE_CLOUD_API_KEY=${google_api_key:-your-google-api-key}
GOOGLE_CLOUD_CLIENT_ID=your-google-client-id
GOOGLE_CLOUD_CLIENT_SECRET=your-google-client-secret

# Facebook Configuration
FACEBOOK_APP_ID=your-facebook-app-id
FACEBOOK_SECRET_KEY=${facebook_secret:-your-facebook-secret}
FACEBOOK_ACCESS_TOKEN=your-facebook-token
EOF

echo ""
echo "================================================"
echo "✅ 로컬 환경 설정 완료!"
echo "================================================"
echo ""
echo "📝 다음 단계:"
echo "   1. .env 파일을 확인하고 필요한 값을 입력하세요"
echo "   2. ./gradlew bootRun 으로 애플리케이션을 실행하세요"
echo ""
echo "⚠️  주의: .env 파일은 Git에 커밋되지 않습니다"
echo ""
