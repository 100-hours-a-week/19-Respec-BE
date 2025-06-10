#!/bin/bash

set -e

echo "[INFO] Starting backend container..."

# 필요한 변수 (이 값은 GitHub Actions에서 sed 또는 envsubst로 삽입하거나 하드코딩할 수도 있음)
AWS_REGION="ap-northeast-2"
ACCOUNT_ID="123456789012"         # 실제 AWS 계정 ID로 바꾸세요
ENV="prod"                        # 예: dev, stage, prod
TAG="prod-abc1234"                # GitHub Actions에서 주입하거나 sed로 치환
REPO_NAME="specranking-backend-${ENV}"
IMAGE="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:${TAG}"

# ECR 로그인
aws ecr get-login-password --region $AWS_REGION \
  | docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# 기존 컨테이너 정리
docker stop backend || true
docker rm backend || true

# Docker 이미지 pull 및 실행
docker pull $IMAGE
docker run -d --name backend -p 8080:8080 $IMAGE

echo "[INFO] Backend container started: $IMAGE"
