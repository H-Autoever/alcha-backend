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
    
    # Kafka에 연결
    def connect_to_kafka(self):
        try:
            self.consumer = KafkaConsumer(
                *self.topic_collection_map.keys(), # topic_collection_map의 키들을 개별 인자로 전달
                bootstrap_servers=self.kafka_servers, # MSK 브로커 주소
                group_id=self.kafka_group_id, # 컨슈머 그룹 ID
                auto_offset_reset='latest'  # 새로운 메시지만 처리 
            )

            print("Kafka 연결 성공")

        except Exception as e:
            print(f"Kafka 연결 실패: {e}")
            raise   # 오류 발생 시 상위로 전달

    # MongoDB에 연결
    def connect_to_mongodb(self):
        try:
            # MongoDB 클라이언트 생성
            self.mongo_client = MongoClient(self.mongo_uri)
            # MongoDB 데이터베이스 선택
            self.db = self.mongo_client[self.mongo_db_name]

            print("MongoDB 연결 성공")
        
        except Exception as e:
            print(f"MongoDB 연결 실패: {e}")
            raise   # 오류 발생 시 상위로 전달


    # 메시지 처리 및 MongoDB 저장
    def process_message(self, message):
        try:
            # 메시지에서 토픽 이름, 실제 데이터 추출
            topic = message.topic
            data = message.value

            # 토픽에 해당하는 MongoDB 컬렉션 선택
            collection_name = self.topic_collection_map[topic]
            collection = self.db[collection_name]

            # MongoDB에 데이터 저장 (단일 문서를 컬렉션에 삽입)
            result = collection.insert_one(data)  
            # 삽입된 문서의 고유 ID 출력
            print(f"데이터 저장 완료: {topic} -> {collection_name}, ID: {result.inserted_id}")

        except Exception as e:
            print(f"메시지 처리 실패: {e}")
           

    # Main 
    def run(self):
        try:
            # Kafka 연결 및 구독, MongoDB 연결
            self.connect_to_kafka()     
            self.connect_to_mongodb()

            print("Vehicle Data Connector 시작...")

            #메시지 처리 루프 (무한 루프로 메시지 대기)
            for message in self.consumer:
                # 각 메시지를 처리하여 MongoDB에 저장
                self.process_message(message)

        except KeyboardInterrupt:
            # 사용자가 중단(Ctrl+C)한 경우
            print("사용자에 의해 중단됨")

        except Exception as e:
            # 기타 오류 발생 시 
            print(f"실행 중 오류 발생: {e}")

        finally:
            # 리소스 정리 (항상 실행됨)
            if hasattr(self, 'consumer'):
                # kafka Consumer 연결 종료 
                self.consumer.close()
            if hasattr(self, 'mongo_client'):
                # MongoDB 연결 종료
                self.mongo_client.close()
            
            print("Vehicle Data Connector 종료...")



if __name__ == "__main__":
    connector = VehicleDataConnector()
    connector.run()