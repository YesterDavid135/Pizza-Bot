package ch.ydavid.pizzabot;

import ch.ydavid.pizzabot.listener.ButtonListener;
import ch.ydavid.pizzabot.listener.JoinListener;
import ch.ydavid.pizzabot.listener.MessageListener;
import ch.ydavid.pizzabot.manager.GeneralManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        String token = "";

        // Read Token
        try (InputStream in = Main.class.getResourceAsStream("/bot.token");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            // Use resource
            token = reader.readLine();
        }

        GeneralManager manager = new GeneralManager();

        //Build JDA
        JDA api = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("you"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(new MessageListener(manager))
                .addEventListeners(new JoinListener(manager))
                .addEventListeners(new ButtonListener(manager))
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_TYPING)
                .build();

        api.awaitReady();

        System.out.println(api.getSelfUser().getName());

        //Add Commands to every Server
        for (Guild guild : api.getGuilds()) {
            if (guild != null) {
                guild.upsertCommand("pizza", "Pizza time").queue();
                guild.upsertCommand("setup", "Setup Dynamic Voice Channels").addOption(OptionType.STRING, "channel", "Name of the new voice channel").addOption(OptionType.STRING, "category", "Name of the category").queue();
                guild.upsertCommand("lock", "Locks your Voice Channel").queue();
                guild.upsertCommand("limit", "Limits your Voice Channel").addOption(OptionType.NUMBER, "limit", "Limit").queue();
                System.out.println("Loaded " + guild.getName());
            }
        }

    }
}