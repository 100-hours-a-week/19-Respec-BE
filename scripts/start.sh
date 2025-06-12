#!/bin/bash
set -e

export AWS_REGION="ap-northeast-2"
export ACCOUNT_ID="115313776476"
export ENV="${ENV:-dev}"
export TAG="${TAG:-}"
export REPO_NAME="specranking-backend-${ENV}"

export CONFIG_BASE="/home/ec2-user/app1/config" # 나중에 app 으로 바꾸세요
export CONFIG_PATH="$CONFIG_BASE/application.properties"
export CONFIG_TEMPLATE_PATH="$CONFIG_BASE/application.properties.template"
export LOG_FILE="/home/ec2-user/backend.log"

echo "🧪 초기 설정 확인"
echo "CONFIG_BASE = $CONFIG_BASE"
echo "CONFIG_TEMPLATE_PATH = $CONFIG_TEMPLATE_PATH"

# 최신 태그 자동 조회
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

echo "📄 application.properties 템플릿 생성 중..."
sudo mkdir -p "$CONFIG_BASE"
sudo chown -R ec2-user:ec2-user "$CONFIG_BASE"

cat <<'EOF' > "$CONFIG_TEMPLATE_PATH"
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
server.port=8080
spring.profiles.active=auth, ai, no-spec-initialize, no-user-initialize, s3
spring.jwt.secret=${SPRING_JWT_SECRET}
spring.security.oauth2.client.registration.kakao.client-id=${KAKAO_CLIENT_ID}
spring.security.oauth2.client.registration.kakao.client-secret=${KAKAO_CLIENT_SECRET}
spring.security.oauth2.client.registration.kakao.redirect-uri=${BACKEND_BASE_URL}/login/oauth2/code/kakao
spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token
spring.cloud.aws.s3.enabled=true
cloud.aws.s3.bucket=${AWS_S3_BUCKET}
cloud.aws.region.static=${AWS_REGION}
cloud.aws.credentials.access-key=${AWS_ACCESS_KEY_ID}
cloud.aws.credentials.secret-key=${AWS_SECRET_ACCESS_KEY}
backend.base-url=${BACKEND_BASE_URL}
frontend.base-url=${FRONTEND_BASE_URL}
ai.server.url=${AI_SERVER_URL}
EOF

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

echo "🚀 백엔드 컨테이너 시작 완료 $DOCKER_IMAGE"
