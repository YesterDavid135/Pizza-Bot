package ch.ydavid.Pizzabot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;


public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw();
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.equalsIgnoreCase("hallo")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Hey " + event.getAuthor().getAsMention()).queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        switch (event.getName()) {
            case "pizza":
                event.reply("Pasta!").setEphemeral(true).queue();
                break;
            case "add":
                if (event.getOptions().size() != 2)
                    event.reply("Please provide 2 Parameters.").setEphemeral(true).queue();

                double n1, n2;
                n1 = event.getOptions().get(0).getAsDouble();
                n2 = event.getOptions().get(1).getAsDouble();

                event.reply(n1 + " + " + n2 + " equals " + (n1 + n2)).setEphemeral(true).queue();
                break;
            case "connect":
                if (event.getGuild() == null)
                    return;
                if (!event.getMember().getVoiceState().inVoiceChannel()) {
                    event.reply("You are not connected to a voice Channel").queue();
                    return;
                }
                AudioManager audioManager = event.getGuild().getAudioManager();

                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                AudioSourceManagers.registerRemoteSources(playerManager);

                AudioPlayer player = playerManager.createPlayer();

                TrackScheduler trackScheduler = new TrackScheduler();
                player.addListener(trackScheduler);


                audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
                audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel());
                audioManager.setSelfDeafened(true);

                playerManager.loadItem("Test", new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        trackScheduler.queue(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        for (AudioTrack track : playlist.getTracks()) {
                            trackScheduler.queue(track);
                        }
                    }

                    @Override
                    public void noMatches() {
                        // Notify the user that we've got nothing
                    }

                    @Override
                    public void loadFailed(FriendlyException throwable) {
                        // Notify the user that everything exploded
                    }
                });


                player.playTrack();

        }
    }

}

