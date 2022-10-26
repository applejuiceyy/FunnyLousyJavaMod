package com.github.applejuiceyy.automa.client.screen_handler_interface;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getInteractionManager;
import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedAnvil extends AutomatedForging<AnvilScreenHandler> {
    public AutomatedAnvil(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, handler);
    }

    public void setRename(String newName) {
        this.handler.setNewItemName(newName);
        getPlayer().networkHandler.sendPacket(new RenameItemC2SPacket(newName));
    }
}
