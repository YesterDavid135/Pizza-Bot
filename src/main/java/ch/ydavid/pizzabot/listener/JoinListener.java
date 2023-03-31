package ch.ydavid.pizzabot.listener;

import ch.ydavid.pizzabot.manager.GeneralManager;
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
        if (manager.getDynamicVoiceManager().isSetup(event.getGuild().getId()))
            manager.getDynamicVoiceManager().createDynamicVoice(event);
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (manager.getDynamicVoiceManager().isSetup(event.getGuild().getId())) {
            manager.getDynamicVoiceManager().createDynamicVoice(event);
            manager.getDynamicVoiceManager().deleteDynamicVoice(event);
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (manager.getDynamicVoiceManager().isSetup(event.getGuild().getId()))
            manager.getDynamicVoiceManager().deleteDynamicVoice(event);
    }


}
