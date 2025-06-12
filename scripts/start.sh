#!/bin/bash
set -e

# Load .env file
ENV_FILE="/home/ec2-user/app1/config/.env"

if [ -f "$ENV_FILE" ]; then
  echo "🔐 Loading environment variables from .env"
  export $(grep -v '^#' "$ENV_FILE" | xargs)
else
  echo "❌ .env file not found at $ENV_FILE"
  exit 1
fi


export CONFIG_BASE="/home/ec2-user/app1/config"
export CONFIG_PATH="$CONFIG_BASE/application.properties"
export CONFIG_TEMPLATE_PATH="$CONFIG_BASE/application.properties.template"
export LOG_FILE="/home/ec2-user/backend.log"

echo "🧪 초기 설정 확인"
echo "CONFIG_BASE = $CONFIG_BASE"
echo "CONFIG_TEMPLATE_PATH = $CONFIG_TEMPLATE_PATH"

if [[ -z "$TAG" ]]; then
  export TAG=$(aws ecr describe-images \
    --repository-name "$REPO_NAME" \
    --region "$AWS_REGION" \
    --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageTags[0]' \
    --output text)
  echo "📦 자동 조회된 최신 ECR 태그: $TAG"
fi

export DOCKER_IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"
echo "🔗 최종 이미지: $DOCKER_IMAGE"

echo "📄 application.properties 치환 생성 중..."
sudo mkdir -p "$CONFIG_BASE"
sudo chown -R ec2-user:ec2-user "$CONFIG_BASE"

if [ ! -f "$CONFIG_TEMPLATE_PATH" ]; then
  echo "❌ ERROR: application.properties.template 파일이 존재하지 않습니다: $CONFIG_TEMPLATE_PATH"
  exit 1
fi

echo "🔁 환경변수 치환 중..."
envsubst < "$CONFIG_TEMPLATE_PATH" > "$CONFIG_PATH"
echo "✅ application.properties 생성 완료: $CONFIG_PATH"

echo "🐳 ECR 로그인 및 컨테이너 재시작"
aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

docker stop backend || true
docker rm backend || true
docker pull "$DOCKER_IMAGE"

docker run -d \
  -e SPRING_CONFIG_LOCATION=file:$CONFIG_PATH \
  -p 8080:8080 \
  -v "$CONFIG_BASE":"$CONFIG_BASE" \
  --name backend "$DOCKER_IMAGE"

echo "🚀 백엔드 컨테이너 시작 완료: $DOCKER_IMAGE"
