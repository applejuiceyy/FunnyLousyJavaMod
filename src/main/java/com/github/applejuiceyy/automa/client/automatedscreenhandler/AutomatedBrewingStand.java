package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.mixin.screenhandler.AbstractFurnaceScreenHandlerAccessor;
import com.github.applejuiceyy.automa.mixin.screenhandler.BrewingStandScreenHandlerAccessor;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedBrewingStand extends AutomatedScreenHandler<BrewingStandScreenHandler> {
    AutomatedBrewingStand(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, (BrewingStandScreenHandler) handler);
    }

    @LuaConvertible
    public DynamicSlotReference left() {
        return new DynamicSlotReference(((BrewingStandScreenHandlerAccessor) handler).getInventory(), 0);
    }

    @LuaConvertible
    public DynamicSlotReference center() {
        return new DynamicSlotReference(((BrewingStandScreenHandlerAccessor) handler).getInventory(), 1);
    }

    @LuaConvertible
    public DynamicSlotReference right() {
        return new DynamicSlotReference(((BrewingStandScreenHandlerAccessor) handler).getInventory(), 2);
    }


    @LuaConvertible
    public DynamicSlotReference potion(@IsIndex int pos) {
        return new DynamicSlotReference(((BrewingStandScreenHandlerAccessor) handler).getInventory(), pos);
    }

    @LuaConvertible
    public DynamicSlotReference ingredient() {
        return new DynamicSlotReference(((BrewingStandScreenHandlerAccessor) handler).getInventory(), 3);
    }

    @LuaConvertible
    public DynamicSlotReference fuel() {
        return new DynamicSlotReference(((BrewingStandScreenHandlerAccessor) handler).getInventory(), 4);
    }


    @LuaConvertible
    public float brewingPercentage() {
        return handler.getBrewTime() / 20f;
    }

    @LuaConvertible
    public float fuelPercentage() {
        return (400 - handler.getBrewTime()) / 400f;
    }
}