package com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory;

import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

@LuaConvertible
public interface ContentQuery {
    @LuaConvertible
    int contains(String id);
    @LuaConvertible
    int contains(LuaFunction func);
    @LuaConvertible
    int count(String id);
    @LuaConvertible
    int count(LuaFunction func);


    @LuaConvertible
    DynamicSlotReference at(@IsIndex int slot);
    @LuaConvertible
    int size();
}
