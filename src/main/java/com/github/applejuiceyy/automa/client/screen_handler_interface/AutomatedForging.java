package com.github.applejuiceyy.automa.client.screen_handler_interface;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.mixin.screenhandler.ForgingScreenHandlerAccessor;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;

@LuaConvertible
public class AutomatedForging<T extends ForgingScreenHandler> extends AutomatedScreenHandler<T> {
    @SuppressWarnings("unchecked")
    public AutomatedForging(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, (T) handler);
    }

    public DynamicSlotReference first() {
        return new DynamicSlotReference(((ForgingScreenHandlerAccessor) handler).getInput(), 0);
    }

    public DynamicSlotReference second() {
        return new DynamicSlotReference(((ForgingScreenHandlerAccessor) handler).getInput(), 1);
    }

    public DynamicSlotReference result() {
        return new DynamicSlotReference(((ForgingScreenHandlerAccessor) handler).getOutput(), 2); // moment
    }
}
