package ch.ydavid.pizzabot.listener;

import ch.ydavid.pizzabot.DAO.GuildConfigDAO;
import ch.ydavid.pizzabot.entity.GuildConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.util.Date;


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
        System.out.println("[" + new Date() + "]" + event.getMember().getUser().getName() + " executed /" + event.getName());
        switch (event.getName()) {
            case "pizza":
                event.getHook().sendMessage("Pasta!").setEphemeral(true).queue();
                break;
            case "lock":
            case "limit":
                limitChannel(event);
                break;
            case "setup":
                GuildConfigDAO configDAO = new GuildConfigDAO();

                GuildConfig gc = configDAO.getEntry(event.getGuild().getId());

                if (gc == null)
                    setupChannel(event, configDAO);
                else
                    setupExistsError(event, gc);
                break;
        }

    }


    private void setupExistsError(SlashCommandEvent event, GuildConfig gc) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Setup error");
        embed.setDescription("You already set up this Guild!");
        embed.setColor(Color.red);
        embed.addField("inline", "hallo", true);
        embed.addField("test", "hallo", false);

        net.dv8tion.jda.api.interactions.components.Button overWriteButton = Button.danger("overwrite", "Overwrite Setup");


        event.getHook().sendMessageEmbeds(embed.build()).addActionRow(overWriteButton).queue();
    }


    private void setupChannel(SlashCommandEvent event, GuildConfigDAO configDAO) {
        EmbedBuilder embed = new EmbedBuilder();

        event.getGuild().createCategory(event.getOption("category").getAsString()).queue(category ->
                category.createVoiceChannel(event.getOption("channel").getAsString()).queue(voiceChannel -> {
                    if (configDAO.insertConfig(new GuildConfig(
                            event.getGuild().getId(),
                            voiceChannel.getId(),
                            category.getId()))) {
                        embed.setTitle("Setup success");
                        embed.setColor(Color.GREEN);
                        embed.setDescription("Join " + voiceChannel.getAsMention() + " to try it out!");
                    } else {
                        embed.setTitle("Setup error");
                        embed.setColor(Color.RED);
                        embed.setDescription("Somewhere happened a error");
                    }

                    event.getHook().sendMessageEmbeds(embed.build()).queue();

                })
        );


    }

    private void limitChannel(SlashCommandEvent event) {
        Member m = event.getMember();

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
    }

}