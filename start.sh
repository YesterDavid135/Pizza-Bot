git pull
mvn package
docker image build -t pizzabot:latest .
docker run --name pizzabot -d pizzabot:latest