package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.InventoryAccess;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

@LuaConvertible
public class AutomatedGeneric extends AutomatedScreenHandler<ScreenHandler> {
    private final int rows;
    private final int columns;
    private final Inventory handlerInventory;

    AutomatedGeneric(LuaExecution executor, ScreenHandler handler, int rows, int columns, Inventory handlerInventory) {
        super(executor, handler);
        this.rows = rows;
        this.columns = columns;
        this.handlerInventory = handlerInventory;
    }
    @LuaConvertible
    public int getRows() {
        return rows;
    }
    @LuaConvertible
    public int getColumns() { return columns; }

    @Property
    public InventoryAccess block() {
        return new InventoryAccess(this, handlerInventory);
    }
}
