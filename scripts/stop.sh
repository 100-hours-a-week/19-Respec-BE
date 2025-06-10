#!/bin/bash

echo "[INFO] Stopping backend container..."
docker stop backend || true
docker rm backend || true
echo "[INFO] Backend container stopped."
