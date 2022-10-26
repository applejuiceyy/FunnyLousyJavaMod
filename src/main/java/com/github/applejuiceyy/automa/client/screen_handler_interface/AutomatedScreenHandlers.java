package com.github.applejuiceyy.automa.client.screen_handler_interface;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.mixin.screenhandler.Generic3x3ContainerScreenHandlerAccessor;
import net.minecraft.screen.*;

import java.util.HashMap;

public class AutomatedScreenHandlers {
    public static HashMap<Class<? extends ScreenHandler>, Factory> map = new HashMap<>(){{
        this.put(GenericContainerScreenHandler.class, (executor, handler) ->
                new AutomatedGeneric(executor, handler, ((GenericContainerScreenHandler) handler).getRows(), 9, ((GenericContainerScreenHandler) handler).getInventory())
        );
        this.put(Generic3x3ContainerScreenHandler.class, (executor, handler) ->
                new AutomatedGeneric(executor, handler, 3, 3, ((Generic3x3ContainerScreenHandlerAccessor) handler).getInventory())
        );

        this.put(LecternScreenHandler.class, AutomatedLectern::new);
        this.put(SmithingScreenHandler.class, AutomatedForging::new);
        this.put(AnvilScreenHandler.class, AutomatedAnvil::new);
    }};

    @FunctionalInterface
    public interface Factory {
        AutomatedScreenHandler<?> create(LuaExecutionFacade executor, ScreenHandler handler);
    }
}
