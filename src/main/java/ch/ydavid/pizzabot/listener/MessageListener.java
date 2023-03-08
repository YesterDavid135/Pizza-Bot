package ch.ydavid.pizzabot.listener;

import ch.ydavid.pizzabot.manager.GeneralManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Date;


public class MessageListener extends ListenerAdapter {

    GeneralManager manager;

    public MessageListener(GeneralManager manager) {
        this.manager = manager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.equalsIgnoreCase("hallo")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Hey " + event.getAuthor().getAsMention()).queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        event.deferReply().queue();
        System.out.println("[" + new Date() + "]" + event.getMember().getUser().getName() + " executed /" + event.getName());
        switch (event.getName()) {
            case "pizza":
                event.getHook().sendMessage("Pasta!").setEphemeral(true).queue();
                break;
            case "lock":
            case "limit":
                manager.getDynamicVoiceManager().limitCommand(event);
                break;
            case "setup":
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR))
                    manager.getDynamicVoiceManager().setupCommand(event);
                else {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Insufficient Permission")
                            .setDescription("Sorry, this is for Administrators only")
                            .setColor(Color.red);

                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }
                break;
            default:
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Unknown Command")
                        .setDescription("Sorry, this command isn't valid anymore")
                        .setColor(Color.red);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
        }

    }


}