#!/bin/bash

# EC2 배포 스크립트
# 목적: EC2 인스턴스에 Kafka Connector 배포

set -e

echo "Vehicle Data Connector 배포 시작..."

# 1. 시스템 업데이트
echo "시스템 패키지 업데이트..."
sudo apt-get update
sudo apt-get upgrade -y

# 2. Docker 설치
echo "Docker 설치..."
sudo apt-get install -y docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker

# 3. 프로젝트 디렉토리 생성
echo "프로젝트 디렉토리 생성..."
sudo mkdir -p /opt/vehicle-data-connector
cd /opt/vehicle-data-connector

# 4. 프로젝트 파일 복사 (Git에서 클론)
echo "프로젝트 파일 복사..."
sudo git clone git@4.230.1.132:root/alcha-backend.git .
cd kafka-connector

# 5. 환경변수 파일 설정
echo "환경변수 설정..."
sudo cp .env.example .env
# 실제 값으로 수정 필요

# 6. Docker 이미지 빌드 및 실행
echo "Docker 이미지 빌드..."
sudo docker-compose build

echo "서비스 시작..."
sudo docker-compose up -d

echo "배포 완료!"
echo "서비스 상태 확인: sudo docker-compose ps"
echo "로그 확인: sudo docker-compose logs -f"