# Kafka Connector for Vehicle Data Pipeline
# 목적: Kafka 토픽에서 차량 데이터를 받아서 MongoDB에 저장

# TODO:
# 1. 필요한 라이브러리 import (kafka-python, pymongo, json, os, dotenv)
# 2. 환경변수 로드 (.env 파일에서 설정 읽기)
# 3. Kafka Consumer 설정 (MSK 연결, SASL/SCRAM 인증)
# 4. MongoDB 연결 설정
# 5. 토픽별 데이터 처리 로직 구현
# 6. 에러 처리 및 로깅 추가
# 
# 구독할 토픽:
# - realtime-storage-data
# - periodic-storage-data  
# - event
# - alerts
