package ch.ydavid.Pizzabot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


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
        }

    }


}