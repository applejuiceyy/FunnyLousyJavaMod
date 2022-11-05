package com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory;

import com.github.applejuiceyy.automa.client.InventoryScreenHelper;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.AutomatedScreenHandler;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;

@LuaConvertible
public record DynamicSlotAction(AutomatedScreenHandler<?> owner, DynamicSlotReference reference,
                                InventoryScreenHelper.ActionAmount action) {
    @LuaConvertible
    public void to(DynamicSlotReference to) {
        InventoryScreenHelper.movePickup(owner.handler,
                reference.inventory(),
                reference.slot(),
                to.inventory(),
                to.slot(),
                action
        );
    }
}
