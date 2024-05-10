mvn -f mainMicroservice clean install package
mvn -f mediaMicroservice clean install package

docker image rm -f media-microservice main-microservice

docker build mainMicroservice/ -t "main-microservice"
docker build mediaMicroservice/ -t "media-microservice"

docker compose up
