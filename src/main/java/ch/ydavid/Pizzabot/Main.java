package ch.ydavid.Pizzabot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException, IOException {

        // Read Token
        File file = new File("src/main/resources/bot.token");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String token = br.readLine();

        //Build JDA
        JDA api = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("Pizza"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(new MessageListener())
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_TYPING)
                .build();

        api.awaitReady();

        //Add Commands to every Server
        for (Guild guild : api.getGuilds()) {
            if (guild != null) {
                guild.upsertCommand("pizza", "Pizza time").queue();
                guild.upsertCommand("add", "Adds numbers").addOption(OptionType.NUMBER, "number1", "Number 1").addOption(OptionType.NUMBER, "number2", "Number 2").queue();
                System.out.println("Added commands to " + guild.getName());
            }
        }
    }
}