package ch.ydavid.pizzabot.listener;

import ch.ydavid.pizzabot.manager.GeneralManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinListener extends ListenerAdapter {

    private GeneralManager manager;

    public JoinListener(GeneralManager manager) {
        this.manager = manager;
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        createDynamicVoice(event);
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        createDynamicVoice(event);
        deleteDynamicVoice(event);
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        deleteDynamicVoice(event);
    }

    private void createDynamicVoice(GenericGuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() == null || !event.getChannelJoined().getId().equals("1081308193143140474")) //Check if User joined the "waiting channel"
            return;

        Guild g = event.getGuild();
        Member m = event.getMember();

        g.createVoiceChannel(m.getUser().getName() + "s Channel", g.getCategoryById("1081308152772972544")).queue(channel -> {
            g.moveVoiceMember(m, channel).queue(); //Move user to the new channel
            System.out.println("Created vc " + channel.getName());
        });

    }

    private void deleteDynamicVoice(GenericGuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() == null || event.getChannelLeft().getParent() == null //check for null objects
                || !event.getChannelLeft().getParent().getId().equals("1081308152772972544") //check if channel was in dynamic voice category
                || event.getChannelLeft().getId().equals("1081308193143140474") //check if channel is the waiting channel
                || event.getChannelLeft().getMembers().size() >= 1) //check if channel is empty
            return;
        System.out.println("Deleted vc " + event.getChannelLeft().getName());
        event.getChannelLeft().delete().queue();
    }

}
