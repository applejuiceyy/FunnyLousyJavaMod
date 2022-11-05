package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getInteractionManager;

@LuaConvertible
public class AutomatedLectern extends AutomatedScreenHandler<LecternScreenHandler>{
    public AutomatedLectern(LuaExecution executor, ScreenHandler handler) {
        super(executor, (LecternScreenHandler) handler);
    }
    @LuaConvertible
    public void setPage(@IsIndex int page) {
        getInteractionManager().clickButton(this.handler.syncId, page + 100);
    }
}
