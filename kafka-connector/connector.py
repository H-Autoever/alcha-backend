import os
import json
from kafka import KafkaConsumer
from pymongo import MongoClient
from dotenv import load_dotenv

# 환경변수 로드
load_dotenv()

class VehicleDataConnector:
    def __init__(self):
        # Kafka 연결 설정
        self.kafka_servers = os.getenv('KAFKA_BOOTSTRAP_SERVERS')
        self.kafka_group_id = os.getenv('KAFKA_GROUP_ID')
        
        # MongoDB 연결 설정
        self.mongo_uri = os.getenv('MONGO_URI')
        self.mongo_db_name = os.getenv('MONGO_DB_NAME')
        
        # 토픽별 컬렉션 매핑
        self.topic_collection_map = {
            'realtime-storage-data': 'realtime_data',
            'periodic-storage-data': 'periodic_data',
            'event': 'events',
            'alerts': 'alerts'
        }
    

    def connect_to_kafka(self):
        """Kafka에 연결"""
        try:
            self.consumer = KafkaConsumer(
                *self.topic_collection_map.keys(), # topic_collection_map의 키들을 개별 인자로 전달달
                bootstrap_servers=self.kafka_servers, # MSK 브로커 주소
                group_id=self.kafka_group_id, # 컨슈머 그룹 ID
                auto_offset_reset='latest'  # 새로운 메시지만 처리 
            )

            print("Kafka 연결 성공")

        except Exception as e:
            print(f"Kafka 연결 실패: {e}")
            raise   # 오류 발생 시 상위로 전달


    def connect_to_mongodb(self):
        """MongoDB에 연결"""
        # TODO: MongoDB 연결 구현
        pass
    
    def process_message(self, message):
        """메시지 처리 및 MongoDB 저장"""
        # TODO: 메시지 처리 로직 구현
        pass
    
    def run(self):
        """메인 실행 함수"""
        # TODO: 전체 실행 로직 구현
        pass

if __name__ == "__main__":
    connector = VehicleDataConnector()
    connector.run()