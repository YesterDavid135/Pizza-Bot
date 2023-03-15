git pull
mvn package
docker container rm pizzabot
docker image build -t pizzabot:latest .
docker run --name pizzabot -d pizzabot:latest