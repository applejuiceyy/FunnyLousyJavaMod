package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.InventoryAccess;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import com.github.applejuiceyy.automa.mixin.screenhandler.PlayerScreenHandlerAccessor;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedPlayerScreen extends AutomatedScreenHandler<PlayerScreenHandler> {
    AutomatedPlayerScreen(LuaExecution executor, ScreenHandler handler) {
        super(executor, (PlayerScreenHandler) handler);
    }

    @Property
    public DynamicSlotReference helmet() {
        return new DynamicSlotReference(this, getPlayer().getInventory(), 39);
    }
    @Property
    public DynamicSlotReference chestplate() {
        return new DynamicSlotReference(this, getPlayer().getInventory(), 38);
    }
    @Property
    public DynamicSlotReference leggings() {
        return new DynamicSlotReference(this, getPlayer().getInventory(), 37);
    }
    @Property
    public DynamicSlotReference boots() {
        return new DynamicSlotReference(this, getPlayer().getInventory(), 36);
    }

    @Property
    public DynamicSlotReference offhand() {
        return new DynamicSlotReference(this, getPlayer().getInventory(), 40);
    }

    @Property
    public InventoryAccess craftingInput() {
        return new InventoryAccess(this, ((PlayerScreenHandlerAccessor) handler).getCraftingInput());
    }

    @Property
    public InventoryAccess craftingResult() {
        return new InventoryAccess(this, ((PlayerScreenHandlerAccessor) handler).getCraftingResult());
    }
}
