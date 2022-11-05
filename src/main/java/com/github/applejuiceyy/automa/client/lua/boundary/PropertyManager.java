package com.github.applejuiceyy.automa.client.lua.boundary;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PropertyManager {
    private final Map<String, MethodWrapper> getters;
    private final Map<String, MethodWrapper> setters;
    private final Map<String, MethodWrapper> methods;
    private final String type;

    public PropertyManager(
            String type,
            Map<String, MethodWrapper> getters,
            Map<String, MethodWrapper> setters,
            Map<String, MethodWrapper> methods
    ) {
        this.type = type;
        this.getters = getters;
        this.setters = setters;
        this.methods = methods;
    }

    public LuaFunction getSetter() {
        return new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs vars) {
                String str = vars.checkjstring(2);

                if (setters.containsKey(str)) {
                    return setters.get(str).invoke(vars.arg1(), vars.arg(3));
                }

                return LuaValue.NIL;
            }
        };
    }

    public LuaFunction getGetter(LuaValue fallback) {
        return genericGetter((o, str) -> {
            LuaValue oldt = o.getmetatable();
            o.setmetatable(fallback);
            LuaValue val;
            try {
                val = o.get(str);
            }
            finally {
                o.setmetatable(oldt);
            }
            return val;
        });
    }

    public LuaFunction getGetter() {
        return genericGetter((o, str) -> LuaValue.NIL);
    }

    LuaFunction genericGetter(BiFunction<LuaValue, String, LuaValue> fallback) {
        return new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs vars) {
                String str = vars.checkjstring(2);

                if (methods.containsKey(str)) {
                    return methods.get(str);
                }

                if (getters.containsKey(str)) {
                    return getters.get(str).invoke(vars.arg1());
                }

                if(Objects.equals(str, "type")) {
                    return LuaValue.valueOf(type);
                }

                return fallback.apply(vars.arg1(), str);
            }
        };
    }

    static PropertyManager from(LuaBoundaryControl owner, String type, HashMap<String, Method> getters, HashMap<String, Method> setters, HashMap<String, ArrayList<Method>> methods) {
        return new PropertyManager(
                type,
                getters.entrySet().stream().collect(Collectors.<Map.Entry<String, Method>, String, MethodWrapper>toMap(Map.Entry::getKey, (e) -> new MethodWrapper(owner, new Method[]{e.getValue()}, false))),
                setters.entrySet().stream().collect(Collectors.<Map.Entry<String, Method>, String, MethodWrapper>toMap(Map.Entry::getKey, (e) -> new MethodWrapper(owner, new Method[]{e.getValue()}, false))),
                methods.entrySet().stream().collect(Collectors.<Map.Entry<String, ArrayList<Method>>, String, MethodWrapper>toMap(Map.Entry::getKey, (e) -> new MethodWrapper(owner, e.getValue().toArray(new Method[0]), Modifier.isStatic(e.getValue().get(0).getModifiers()))))
        );
    }
}
