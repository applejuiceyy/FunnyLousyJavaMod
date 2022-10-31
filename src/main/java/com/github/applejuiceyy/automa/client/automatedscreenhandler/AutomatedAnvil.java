package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.mixin.screenhandler.ForgingScreenHandlerAccessor;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedAnvil extends Generic2ItemMerger<AnvilScreenHandler> {
    public AutomatedAnvil(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, (AnvilScreenHandler) handler,
                () -> new DynamicSlotReference(((ForgingScreenHandlerAccessor) handler).getInput(), 0),
                () -> new DynamicSlotReference(((ForgingScreenHandlerAccessor) handler).getInput(), 1),
                () -> new DynamicSlotReference(((ForgingScreenHandlerAccessor) handler).getOutput(), 0)
        );
    }
    @LuaConvertible
    public void setRename(String newName) {
        this.handler.setNewItemName(newName);
        getPlayer().networkHandler.sendPacket(new RenameItemC2SPacket(newName));
    }

    @LuaConvertible
    public int getCost() {
        return handler.getLevelCost();
    }
}
