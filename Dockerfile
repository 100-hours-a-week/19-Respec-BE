- name: Build and push Docker image
  run: |
    IMAGE="${{ env.ACCOUNT_ID }}.dkr.ecr.${{ env.AWS_REGION }}.amazonaws.com/${{ env.REPO_NAME }}:${{ env.TAG }}"
    docker build -t "$IMAGE" .
    docker push "$IMAGE"
