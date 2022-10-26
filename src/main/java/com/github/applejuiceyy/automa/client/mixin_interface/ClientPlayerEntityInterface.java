package com.github.applejuiceyy.automa.client.mixin_interface;

import com.github.applejuiceyy.automa.client.screen_handler_interface.AutomatedScreenHandler;
import org.jetbrains.annotations.Nullable;

public interface ClientPlayerEntityInterface {
    AutomatedScreenHandler<?> getAutomatedScreenHandler();
    void setAutomatedScreenHandler(@Nullable AutomatedScreenHandler<?> handler);
}
