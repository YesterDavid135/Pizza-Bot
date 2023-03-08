package ch.ydavid.pizzabot.manager;

import ch.ydavid.pizzabot.DAO.GuildConfigDAO;
import ch.ydavid.pizzabot.entity.GuildConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;

public class DynamicVoiceManager {
    GuildConfigDAO configDAO = new GuildConfigDAO();

    public void setupCommand(SlashCommandEvent event) {

        GuildConfigDAO configDAO = new GuildConfigDAO();

        GuildConfig gc = configDAO.getEntry(event.getGuild().getId());

        if (gc == null)
            setupChannel(event, configDAO);
        else
            setupExistsError(event, gc);
    }

    public void setupChannel(SlashCommandEvent event, GuildConfigDAO configDAO) {
        EmbedBuilder embed = new EmbedBuilder();

        event.getGuild().createCategory(event.getOption("category").getAsString()).queue(category ->
                category.createVoiceChannel(event.getOption("channel").getAsString()).queue(voiceChannel -> {
                    if (configDAO.peristConfig(new GuildConfig(
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

    public void setupExistsError(SlashCommandEvent event, GuildConfig gc) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Setup error");
        embed.setDescription("You already set up this Guild!");
        embed.setColor(Color.red);
        if (event.getGuild().getVoiceChannelById(gc.getNewVCId()) == null)
            embed.addField("Current Voice Channel", "Channel not found", true);
        else
            embed.addField("Current Voice Channel", event.getGuild().getVoiceChannelById(gc.getNewVCId()).getAsMention(), true);
        embed.addField("New Voice Channel", event.getOption("channel").getAsString(), false);
        embed.addField("New Category", event.getOption("category").getAsString(), true);

        net.dv8tion.jda.api.interactions.components.Button overWriteButton = Button.danger("setup-overwrite", "Overwrite Setup");

        event.getHook().sendMessageEmbeds(embed.build()).addActionRow(overWriteButton).queue();
    }

    public void overwriteSetup(ButtonClickEvent event) {
        GuildConfig gc = configDAO.getEntry(event.getGuild().getId());
        String voice = event.getMessage().getEmbeds().get(0).getFields().get(1).getValue();
        String categoryName = event.getMessage().getEmbeds().get(0).getFields().get(2).getValue();
        event.getGuild().createCategory(categoryName).queue(category ->
                category.createVoiceChannel(voice).queue(voiceChannel -> {
                    gc.setCategoryID(category.getId());
                    gc.setNewVCId(voiceChannel.getId());
                    EmbedBuilder embed = new EmbedBuilder();
                    if (configDAO.peristConfig(gc)) {
                        embed.setTitle("Setup success");
                        embed.setColor(Color.GREEN);
                        embed.setDescription("Join " + voiceChannel.getAsMention() + " to try it out!");
                    } else {
                        embed.setTitle("Setup error");
                        embed.setColor(Color.RED);
                        embed.setDescription("Somewhere happened a error");
                    }
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }));

    }

    public void limitCommand(SlashCommandEvent event) {
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
