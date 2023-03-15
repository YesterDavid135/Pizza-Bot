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
        manager.getDynamicVoiceManager().createDynamicVoice(event);
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        manager.getDynamicVoiceManager().createDynamicVoice(event);
        manager.getDynamicVoiceManager().deleteDynamicVoice(event);
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        manager.getDynamicVoiceManager().deleteDynamicVoice(event);
    }




}
