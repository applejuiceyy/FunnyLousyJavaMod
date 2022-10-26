package com.github.applejuiceyy.automa.client.screen_handler_interface;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getInteractionManager;

@LuaConvertible
public class AutomatedLectern extends AutomatedScreenHandler<LecternScreenHandler>{
    public AutomatedLectern(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, (LecternScreenHandler) handler);
    }

    public void setPage(int page) {
        getInteractionManager().clickButton(this.handler.syncId, page + 100);
    }
}
