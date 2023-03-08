package ch.ydavid.pizzabot.listener;

import ch.ydavid.pizzabot.manager.GeneralManager;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends ListenerAdapter {
    private GeneralManager manager;

    public ButtonListener(GeneralManager manager) {
        this.manager = manager;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        event.deferReply().queue();
        switch (event.getButton().getId()){
            case "setup-overwrite":
                manager.getDynamicVoiceManager().overwriteSetup(event);
        }

    }
}
