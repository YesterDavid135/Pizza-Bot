git pull
mvn package
docker image build -t pizzabot:latest .
docker run -d pizzabot:latest

