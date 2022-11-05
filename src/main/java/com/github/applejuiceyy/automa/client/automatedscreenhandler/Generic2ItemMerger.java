package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Function;
import java.util.function.Supplier;

@LuaConvertible
public class Generic2ItemMerger<T extends ScreenHandler> extends AutomatedScreenHandler<T> {
    private final Function<Generic2ItemMerger<?>, DynamicSlotReference> first;
    private final Function<Generic2ItemMerger<?>, DynamicSlotReference> second;
    private final Function<Generic2ItemMerger<?>, DynamicSlotReference> result;

    Generic2ItemMerger(LuaExecution executor, T handler,
                       Function<Generic2ItemMerger<?>, DynamicSlotReference> first,
                       Function<Generic2ItemMerger<?>, DynamicSlotReference> second,
                       Function<Generic2ItemMerger<?>, DynamicSlotReference> result
    ) {
        super(executor, handler);
        this.first = first;
        this.second = second;
        this.result = result;
    }

    @Property
    public DynamicSlotReference first() {
        return first.apply(this);
    }

    @Property
    public DynamicSlotReference second() {
        return second.apply(this);
    }

    @Property
    public DynamicSlotReference result() {
        return result.apply(this);
    }
}
