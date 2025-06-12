#!/bin/bash
set -e

AWS_REGION="ap-northeast-2"
ACCOUNT_ID="115313776476"
ENV="${ENV:-dev}"
TAG="${TAG:-}"
REPO_NAME="specranking-backend-${ENV}"

CONFIG_BASE="/home/ec2-user/app1/config" # 나중에 app 으로 바꾸세요
CONFIG_PATH="$CONFIG_BASE/application.properties"
CONFIG_TEMPLATE_PATH="$CONFIG_BASE/application.properties.template"
LOG_FILE="/home/ec2-user/backend.log"

# 최신 태그 조회
if [[ -z "$TAG" ]]; then
  TAG=$(aws ecr describe-images \
    --repository-name "$REPO_NAME" \
    --region "$AWS_REGION" \
    --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageTags[0]' \
    --output text)
  echo "📦 최신 ECR 태그: $TAG"
fi

IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"

echo "✅ ENV=$ENV"
echo "✅ REPO_NAME=$REPO_NAME"
echo "✅ TAG=$TAG"
echo "Account_ID $ACCOUNT_ID AWS_REGION $AWS_REGION REPO_NAME $REPO_NAME TAG $TAG"

echo "📌 RAW: ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"
IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"
echo "🔗 IMAGE=$IMAGE"





echo "📦 application.properties 템플릿 생성 중..."
sudo mkdir -p "$CONFIG_BASE"
sudo chown -R ec2-user:ec2-user "$CONFIG_BASE"

cat <<'EOF' > "$CONFIG_TEMPLATE_PATH"
# ... (생략 없이 동일)
EOF

echo "🔁 환경변수 치환 중..."
envsubst < "$CONFIG_TEMPLATE_PATH" > "$CONFIG_PATH"
echo "✅ application.properties 생성 완료"

aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

docker stop backend || true
docker rm backend || true
docker pull "$IMAGE"

docker run -d \
  -e SPRING_CONFIG_LOCATION=file:$CONFIG_PATH \
  -p 8080:8080 \
  -v "$CONFIG_BASE":"$CONFIG_BASE" \
  --name backend "$IMAGE"
