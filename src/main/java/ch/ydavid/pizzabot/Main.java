package ch.ydavid.pizzabot;

import ch.ydavid.pizzabot.entity.Person;
import ch.ydavid.pizzabot.listener.JoinListener;
import ch.ydavid.pizzabot.listener.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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

        //Build JDA
        JDA api = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("you"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(new MessageListener())
                .addEventListeners(new JoinListener())
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_TYPING)
                .build();

        api.awaitReady();

        //Add Commands to every Server
        for (Guild guild : api.getGuilds()) {
            if (guild != null) {
                guild.upsertCommand("pizza", "Pizza time").queue();
                guild.upsertCommand("lock", "Locks your Voice Channel").queue();
                guild.upsertCommand("limit", "Limits your Voice Channel").addOption(OptionType.NUMBER, "limit", "Limit").queue();
                System.out.println("Loaded " + guild.getName());
            }
        }
        EntityManagerFactory entityManagerFactory;

        entityManagerFactory = Persistence.createEntityManagerFactory("ch.ydavid.pizzabot");

        Person person = new Person();

        person.setName("David");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.persist(person);
    }
}