## Pizzabot

A simple Discord Bot to test stuff

### Setup

1. Create a Discord Application on the [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a Bot and copy it's token
3. Paste the Bot token into [src/main/resources/bot.token](src/main/resources/bot.token)

### Run

1. Make Maven package

```shell
    mvn package 
```

2. Build Docker Image

```shell
docker image build -t pizzabot:latest .
```

3. Run Docker Container

```shell
docker run -d pizzabot:latest
```