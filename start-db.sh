docker pull mysql/mysql-server:latest
docker run --name=pizzabot-db -d mysql/mysql-server:latest \
-e MYSQL_ROOT_PASSWORD=pizza \
-e MYSQL_DATABASE=pizzabot \
-e MYSQL_USER=pizzabot \
-e MYSQL_PASSWORD=pizza \
--publish 3306:3306




docker run --name=pizzabot-db -e MYSQL_ROOT_PASSWORD="pizza" -e MYSQL_USER="pizzabot" -e MYSQL_PASSWORD="pizza" --publish 3306:3306 mysql


