package ch.ydavid.pizzabot.listener;

import ch.ydavid.pizzabot.manager.GeneralManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ButtonListener extends ListenerAdapter {
    private GeneralManager manager;

    public ButtonListener(GeneralManager manager) {
        this.manager = manager;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        event.deferReply().queue();
        switch (event.getButton().getId()) {
            case "setup-overwrite":
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR))
                    manager.getDynamicVoiceManager().overwriteSetup(event);
                else {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Insufficient Permission")
                            .setDescription("Sorry, this is for Administrators only")
                            .setColor(Color.red);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }

        }
    }
}
