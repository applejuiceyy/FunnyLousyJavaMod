package com.github.applejuiceyy.automa.client.screen_handler_interface;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedGeneric extends AutomatedScreenHandler<ScreenHandler> {
    private final int rows;
    private final int columns;
    private final Inventory handlerInventory;

    AutomatedGeneric(LuaExecutionFacade executor, ScreenHandler handler, int rows, int columns, Inventory handlerInventory) {
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
    @LuaConvertible
    public DynamicSlotReference block(@IsIndex int slot) {
        return new DynamicSlotReference(handlerInventory, slot);
    }
}
