#!/bin/bash
set -e

export CONFIG_BASE="/home/ec2-user/app1/config"
export CONFIG_PATH="$CONFIG_BASE/application.properties"
export CONFIG_TEMPLATE_PATH="$CONFIG_BASE/application.properties.template"
export LOG_FILE="/home/ec2-user/backend.log"

echo "🔐 .env 파일 로딩 중..."
if [ -f "$CONFIG_BASE/.env" ]; then
  export $(grep -v '^#' "$CONFIG_BASE/.env" | xargs)
  echo "✅ .env 파일 로딩 완료"
else
  echo "⚠️ .env 파일이 없습니다: $CONFIG_BASE/.env"
fi

# 디버깅용 출력
echo "🔥 ENV = $ENV"
echo "🔥 TAG = $TAG"
echo "🔥 REPO_NAME = $REPO_NAME"

# 기본값 보완
export AWS_REGION="${AWS_REGION:-ap-northeast-2}"
export ACCOUNT_ID="${ACCOUNT_ID:-115313776476}"
export ENV="${ENV:-dev}"
export TAG="${TAG:-dev-manual}"
export REPO_NAME="${REPO_NAME:-specranking-backend-${ENV}}"

# ECR 이미지 태그 조회 (TAG가 없으면 조회)
if [[ -z "$TAG" || "$TAG" == "dev-manual" ]]; then
  echo "🔍 ECR 최신 태그 조회 중..."
  TAG=$(aws ecr describe-images \
    --repository-name "$REPO_NAME" \
    --region "$AWS_REGION" \
    --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageTags[0]' \
    --output text)
  echo "📦 조회된 최신 태그: $TAG"
fi

export DOCKER_IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"
echo "🔗 사용할 이미지: $DOCKER_IMAGE"

# properties 생성
echo "📄 application.properties 치환 생성 중..."
sudo mkdir -p "$CONFIG_BASE"
sudo chown -R ec2-user:ec2-user "$CONFIG_BASE"

if [ ! -f "$CONFIG_TEMPLATE_PATH" ]; then
  echo "❌ ERROR: application.properties.template이 없습니다: $CONFIG_TEMPLATE_PATH"
  exit 1
fi

envsubst < "$CONFIG_TEMPLATE_PATH" > "$CONFIG_PATH"
echo "✅ application.properties 생성 완료: $CONFIG_PATH"

# Docker 재시작
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
