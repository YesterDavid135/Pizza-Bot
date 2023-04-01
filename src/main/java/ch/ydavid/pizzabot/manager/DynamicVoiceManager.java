package ch.ydavid.pizzabot.manager;

import ch.ydavid.pizzabot.DAO.GuildConfigDAO;
import ch.ydavid.pizzabot.entity.GuildConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.util.EnumSet;

public class DynamicVoiceManager {
    GuildConfigDAO configDAO = new GuildConfigDAO();

    public void setupCommand(SlashCommandEvent event) {

        if (isSetup(event.getGuild().getId()))
            setupExistsError(event);
        else
            setupChannel(new GuildConfig(), event.getOption("channel").getAsString(), event.getOption("category").getAsString(), event.getGuild(), event.getHook());


    }

    public boolean isSetup(String guildID) {
        return configDAO.checkIfConfigExists(guildID);
    }

    public void setupChannel(GuildConfig config, String channelName, String categoryName, Guild guild, InteractionHook hook) {
        EmbedBuilder embed = new EmbedBuilder();
        config.setGuildId(guild.getId());

        guild.createCategory(categoryName).queue(category ->
                category.createVoiceChannel(channelName).queue(voiceChannel -> {
                    config.setCategoryID(category.getId());
                    config.setNewVCId(voiceChannel.getId());
                    if (config.getGuildId() != null) {
                        if (configDAO.mergeConfig(config)) {
                            embed.setTitle("Setup success");
                            embed.setColor(Color.GREEN);
                            embed.setDescription("Join " + voiceChannel.getAsMention() + " to try it out!");
                        }
                    } else if (configDAO.peristConfig(config)) {
                        embed.setTitle("Setup success");
                        embed.setColor(Color.GREEN);
                        embed.setDescription("Join " + voiceChannel.getAsMention() + " to try it out!");
                    } else {
                        embed.setTitle("Setup error");
                        embed.setColor(Color.RED);
                        embed.setDescription("Somewhere happened a error");
                    }

                    hook.sendMessageEmbeds(embed.build()).queue();

                })
        );
    }

    public void setupExistsError(SlashCommandEvent event) {
        GuildConfig gc = configDAO.getEntry(event.getGuild().getId());

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

        String channel = event.getMessage().getEmbeds().get(0).getFields().get(1).getValue();
        String category = event.getMessage().getEmbeds().get(0).getFields().get(2).getValue();

        setupChannel(gc, channel, category, event.getGuild(), event.getHook());
        event.getMessage().delete().queue();
        event.getChannel().sendMessage("Setup overwritten!").queue();
//        System.out.println(event.getMessage().getId());
    }

    private boolean checkIfChannelOwner(SlashCommandEvent event) {
        Member m = event.getMember();

        EmbedBuilder embed = new EmbedBuilder();
        if (!m.getVoiceState().inVoiceChannel()) {
            embed.setColor(Color.red);
            embed.setDescription("You are not in a voice channel");
            event.getHook().sendMessageEmbeds(embed.build()).queue();
            return false;
        }
        VoiceChannel vc = m.getVoiceState().getChannel();
        if (!vc.getName().startsWith(m.getUser().getName())) {
            embed.setColor(Color.red);
            embed.setDescription("This isn't your channel");
            event.getHook().sendMessageEmbeds(embed.build()).queue();
            return false;
        }
        return true;
    }

    public void limitCommand(SlashCommandEvent event) {
        if (!checkIfChannelOwner(event))
            return;

        Member m = event.getMember();
        VoiceChannel vc = m.getVoiceState().getChannel();

        EmbedBuilder embed = new EmbedBuilder();

        if (event.getName().equals("lock")) {
            if (vc.getUserLimit() == vc.getMembers().size()) {
                vc.getManager().setUserLimit(0).queue();
                embed.setDescription("Unlocked your voice channel!");
            } else {
                vc.getManager().setUserLimit(vc.getMembers().size()).queue();
                embed.setDescription("Locked your voice channel!");
            }
        } else {
            int option = (int) event.getOptions().get(0).getAsDouble();
            if (option > 99 || option < -1) {
                embed.setDescription("Please use a range between -1 and 99");
                embed.setColor(Color.red);
            } else {
                vc.getManager().setUserLimit(option).queue();
                embed.setDescription("Limited your voice channel!");
                embed.setColor(Color.green);
            }
        }
        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }

    public void createDynamicVoice(GenericGuildVoiceUpdateEvent event) {
        GuildConfig config = configDAO.getEntry(event.getGuild().getId()); //Get GuildConfig from Database

        if (event.getChannelJoined() == null || !event.getChannelJoined().getId().equals(config.getNewVCId())) //Check if User joined the "waiting channel"
            return;

        Guild g = event.getGuild();
        Member m = event.getMember();

        g.createVoiceChannel(m.getUser().getName() + "s Channel", g.getCategoryById(config.getCategoryID())).queue(channel -> {
            g.moveVoiceMember(m, channel).queue(); //Move user to the new channel
            System.out.println("Created vc " + channel.getName());
        });
    }

    public void deleteDynamicVoice(GenericGuildVoiceUpdateEvent event) {
        GuildConfig config = configDAO.getEntry(event.getGuild().getId()); //Get GuildConfig from Database

        if (event.getChannelLeft() == null || event.getChannelLeft().getParent() == null //check for null objects
                || !event.getChannelLeft().getParent().getId().equals(config.getCategoryID()) //check if channel was in dynamic voice category
                || event.getChannelLeft().getId().equals(config.getNewVCId()) //check if channel is the waiting channel
                || event.getChannelLeft().getMembers().size() >= 1) //check if channel is empty
            return;
        System.out.println("Deleted vc " + event.getChannelLeft().getName());
        event.getChannelLeft().delete().queue();
    }


    public void hideCommand(SlashCommandEvent event) {
        if (!checkIfChannelOwner(event))
            return;

        Member m = event.getMember();
        VoiceChannel vc = m.getVoiceState().getChannel();

        Role role = event.getGuild().getPublicRole();
        EnumSet<Permission> permissions = EnumSet.of(Permission.VIEW_CHANNEL);


        if (vc.getPermissionOverride(role) != null && vc.getPermissionOverride(role).getDenied().contains(Permission.VIEW_CHANNEL)) {
            vc.getManager().putPermissionOverride(role, permissions, null).queue();
            event.reply("Unhidden your channel!").setEphemeral(true).queue();
        } else {
            vc.getManager().putPermissionOverride(role, null, permissions).queue();
            event.reply("Hidden your chanel!").setEphemeral(true).queue();
        }


    }
}
