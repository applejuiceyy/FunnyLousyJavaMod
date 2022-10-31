package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.mixin.screenhandler.CartographyTableScreenHandlerAccessor;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Supplier;

@LuaConvertible
public class Generic2ItemMerger<T extends ScreenHandler> extends AutomatedScreenHandler<T> {
    private final Supplier<DynamicSlotReference> first;
    private final Supplier<DynamicSlotReference> second;
    private final Supplier<DynamicSlotReference> result;

    Generic2ItemMerger(LuaExecutionFacade executor, T handler,
                       Supplier<DynamicSlotReference> first,
                       Supplier<DynamicSlotReference> second,
                       Supplier<DynamicSlotReference> result
    ) {
        super(executor, handler);
        this.first = first;
        this.second = second;
        this.result = result;
    }

    @LuaConvertible
    public DynamicSlotReference first() {
        return first.get();
    }

    @LuaConvertible
    public DynamicSlotReference second() {
        return second.get();
    }

    @LuaConvertible
    public DynamicSlotReference result() {
        return result.get();
    }
}
