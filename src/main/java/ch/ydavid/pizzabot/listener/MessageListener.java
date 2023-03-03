package ch.ydavid.pizzabot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;


public class MessageListener extends ListenerAdapter {
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
        switch (event.getName()) {
            case "pizza":
                event.getHook().sendMessage("Pasta!").setEphemeral(true).queue();
                break;
            case "lock":
            case "limit":
                Member m = event.getMember();
                System.out.println(m.getUser().getName() + " executed /lock");
                EmbedBuilder embed = new EmbedBuilder();
                if (!m.getVoiceState().inVoiceChannel()) {
                    embed.setColor(Color.red);
                    embed.setDescription("You are not in a voice channel");
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                    return;
                }
                VoiceChannel vc = m.getVoiceState().getChannel();
                if (!vc.getName().startsWith(m.getUser().getName())) {
                    embed.setColor(Color.red);
                    embed.setDescription("This isn't your channel");
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                    return;
                }

                if (event.getName().equals("lock")) {
                    if (vc.getUserLimit() == vc.getMembers().size()) {
                        vc.getManager().setUserLimit(0).queue();
                        embed.setDescription("Unlocked your voice channel!");
                    } else {
                        vc.getManager().setUserLimit(vc.getMembers().size()).queue();
                        embed.setDescription("Locked your voice channel!");
                    }
                } else {
                    vc.getManager().setUserLimit((int) event.getOptions().get(0).getAsDouble()).queue();
                    embed.setDescription("Limited your voice channel!");

                }
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                break;
        }

    }

}