package com.github.applejuiceyy.automa.client.lua;

import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Require extends OneArgFunction {
    private final LuaExecutionFacade executor;
    private final HashMap<String, LuaValue> cache = new LinkedHashMap<>();
    private final LinkedList<String> requireTrace = new LinkedList<>();

    Require(LuaExecutionFacade executor) {
        this.executor = executor;
    }

    @Override
    public LuaValue call(LuaValue arg) {
        String name = arg.checkjstring();

        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        if(requireTrace.contains(name)) {
            throw new LuaError("Cyclic dependency");
        }

        requireTrace.add(name);
        LuaValue ret;

        try {
            ret = executor.loadFile(name);
        } catch (FileNotFoundException | NoSuchFileException err) {
            try {
                ret = executor.loadResource(name);
            } catch (FileNotFoundException ex) {
                throw new LuaError("Cannot find file");
            }
        }

        ret = ret.call();

        requireTrace.removeLast();
        cache.put(name, ret);
        return ret;
    }
}
