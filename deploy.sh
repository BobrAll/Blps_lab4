mvn -f blps clean install package
mvn -f mediaMicroservice clean install package

docker build blps/ -t "backend"
docker build mediaMicroservice/ -t "media-microservice"
docker compose up
