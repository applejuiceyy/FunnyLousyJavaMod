package com.github.applejuiceyy.automa.client.lua;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Metatable;
import com.github.applejuiceyy.automa.client.lua.api.Inventory;
import com.github.applejuiceyy.automa.client.lua.api.Player;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import com.github.applejuiceyy.automa.client.lua.api.world.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

import static com.github.applejuiceyy.automa.client.AutomaClient.LOGGER;

public class LuaBoundaryControl {
    private final HashMap<Class<?>, LuaTable> cache = new HashMap<>();
    private final LuaExecutionFacade owner;

    static Class<?>[] loadClasses = {
            LuaEvent.class,
            LuaEvent.CancellationState.class,
            Inventory.class,
            Player.class,

            ItemStackWrap.class,
            World.class,
            BlockWrap.class,
            BlockStateWrap.class,
            ItemWrap.class
    };

    static HashMap<Class<?>, Class<? extends Wrapper<?>>> wrappers = new HashMap<>(){{
        this.put(Block.class, BlockWrap.class);
        this.put(BlockState.class, BlockStateWrap.class);
        this.put(ItemStack.class, ItemStackWrap.class);
        this.put(Item.class, ItemWrap.class);
    }};

    public LuaBoundaryControl(LuaExecutionFacade owner) {
        this.owner = owner;
    }

    public void loadAllClasses() {
        for (Class<?> cls: loadClasses) {
            buildClass(cls);
        }
    }

    public LuaTable buildClass(Class<?> cls) {
        if (cache.containsKey(cls)) {
            return cache.get(cls);
        }

        LuaTable indexing = new LuaTable();
        LuaTable metatable = new LuaTable();

        LuaValue constructor = new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                Object[] converted = LVarargs2J(args);

                Constructor<?> constructor;

                try {
                    constructor = cls.getConstructor((Class<?>[]) Arrays.stream(converted).map(Object::getClass).toArray());
                } catch (NoSuchMethodException e) {
                    throw new LuaError("Unknown constructor");
                }

                Object instance;
                try {
                    instance = constructor.newInstance(converted);
                } catch (Exception e) {
                    // TODO: more constructive error
                    throw new LuaError("Something happened while creating instance");
                }

                return J2L(instance);
            }
        };

        for(Method method: cls.getDeclaredMethods()) {
            if(Modifier.isPublic(method.getModifiers())) {
                String name = method.getName();

                if (method.getName().equals("getInstanceFactory")) {
                    try {
                        constructor = J2L(method.invoke(null, this.owner));
                    } catch (Exception e) {
                        LOGGER.error(String.format("Constructor factory of class %s failed", cls.getName()));
                    }
                }

                if (method.isAnnotationPresent(Metatable.class)) {
                    metatable.set(name, buildMethod(method));
                }
                else {
                    indexing.set(name, buildMethod(method));
                }
            }
        }

        indexing.set("new", constructor);

        for(Class<?> classes: cls.getDeclaredClasses()) {
            if (classes.isAnnotationPresent(LuaConvertible.class) && Modifier.isPublic(classes.getModifiers())) {
                indexing.set(classes.getName(), buildClass(classes));
            }
        }

        Class<?> supercls = cls.getSuperclass();

        if (supercls != Object.class) {
            if (supercls.isAnnotationPresent(LuaConvertible.class)) {
                LuaTable supermeta = buildClass(supercls);
                metatable.setmetatable(supermeta);
                LuaTable indexMeta = new LuaTable();
                indexMeta.set("__index", supermeta.getmetatable().get("__index"));
                indexing.setmetatable(supermeta);
            }
        }

        metatable.set("__index", indexing);

        cache.put(cls, metatable);

        return metatable;
    }

    public LuaFunction buildMethod(Method method) {
        return new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                // the first argument is stored separately so we don't have to resize
                // the array
                boolean isStatic = Modifier.isStatic(method.getModifiers());

                Object ret;

                try {
                    if (isStatic) {
                        Object[] converted = LVarargs2J(args);
                        ret = method.invoke(null, converted);
                    }
                    else {
                        FirstSeparatedArray<Object> converted = LVarargsSeparated2J(args, false);
                        ret = method.invoke(converted.first, converted.rest);
                    }
                } catch (IllegalAccessException e) {
                    throw new LuaError("Cannot access method");
                } catch (InvocationTargetException e) {
                    throw new LuaError("Incorrect instance");
                }

                return J2L(ret);
            }
        };
    }

    public Object L2J(LuaValue val) {
        return L2J(val, true);
    }

    public Object L2J(LuaValue val, boolean unwrap) {
        if (val.istable())
            return val.checktable();
        else if (val.isnumber())
            if (val instanceof LuaInteger i)
                return i.checkint();
            else if (val.isint() && val instanceof LuaString s)
                return s.checkint();
            else
                return val.checkdouble();
        else if (val.isstring())
            return val.checkjstring();
        else if (val.isboolean())
            return val.checkboolean();
        else if (val.isfunction())
            return val.checkfunction();
        else if (val.isuserdata()) {
            Object obj = val.checkuserdata(Object.class);
            if (unwrap && Arrays.stream(obj.getClass().getInterfaces()).anyMatch((p) -> p == Wrapper.class)) {
                return ((Wrapper<?>) obj).getWrapped();
            }
            return obj;
        }
        else
            return null;
    }

    public FirstSeparatedArray<Object> LVarargsSeparated2J(Varargs args, boolean unwrapFirst) {
        Object[] converted = new Object[args.narg() - 1];
        Object first = null;

        for (int i = 0; i < args.narg(); i++) {
            if (i == 0) {
                first = L2J(args.arg(i + 1), unwrapFirst);
            }
            else {
                converted[i - 1] = L2J(args.arg(i + 1));
            }
        }

        return new FirstSeparatedArray<>(first, converted);
    }

    public Object[] LVarargs2J(Varargs args) {
        Object[] converted = new Object[args.narg()];

        for (int i = 0; i < args.narg(); i++) {
            converted[i] = L2J(args.arg(i + 1));
        }

        return converted;
    }

    public LuaValue J2L(Object val) {
        if (val == null) {
            return LuaValue.NIL;
        }

        {
            Class<?> current = val.getClass();

            while (current != Object.class) {
                if (wrappers.containsKey(current)) {
                    Class<?> wrapping = wrappers.get(current);
                    Constructor<?> constructor;
                    try {
                        constructor = wrapping.getConstructor(current);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("Wrapper class has no valid constructor");
                    }

                    try {
                        val = constructor.newInstance(val);
                        break;
                    } catch (InstantiationException e) {
                        throw new RuntimeException("Attempt at instancing a wrapper instance failed");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Wrapper constructor is not accessible");
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("Failed to create instance");
                    }
                }

                current = current.getSuperclass();
            }
        }

        if (val instanceof LuaValue l)
            return l;
        else if (val instanceof Double d)
            return LuaValue.valueOf(d);
        else if (val instanceof String s)
            return LuaValue.valueOf(s);
        else if (val instanceof Boolean b)
            return LuaValue.valueOf(b);
        else if (val instanceof Integer i)
            return LuaValue.valueOf(i);
        else if (val instanceof Float f)
            return LuaValue.valueOf(f);
        else if (val instanceof Byte b)
            return LuaValue.valueOf(b);
        else if (val instanceof Long l)
            return LuaValue.valueOf(l);
        else if (val instanceof Character c)
            return LuaValue.valueOf(c);
        else if (val instanceof Short s)
            return LuaValue.valueOf(s);
        else {
            Class<?> cls = val.getClass();

            for(Class<?> interf: cls.getInterfaces()) {
                if (!interf.isAnnotationPresent(FunctionalInterface.class)) {
                    continue;
                }

                for (Method method : interf.getMethods()) {
                    int modifiers = method.getModifiers();
                    if (Modifier.isAbstract(modifiers)) {
                        Object finalVal = val;
                        return new VarArgFunction() {
                            @Override
                            public Varargs invoke(Varargs args) {
                                // repeated code, but I cannot think of a good way to extract it
                                Object[] converted = LVarargs2J(args);

                                Object ret;
                                try {
                                    ret = method.invoke(finalVal, converted);
                                } catch (IllegalAccessException e) {
                                    throw new LuaError("Cannot access method");
                                } catch (InvocationTargetException e) {
                                    throw new LuaError("Incorrect instance");
                                }

                                return J2L(ret);
                            }
                        };
                    }
                }

                throw new RuntimeException("FunctionalInterface does not have correct methods");
            }

            return wrapObject(val);
        }
    }

    public LuaUserdata wrapObject(Object obj) {
        Class<?> clazz = obj.getClass();
        LuaTable metatable = cache.get(clazz);
        if (metatable == null) {
            throw new RuntimeException("Illegal object attempt \"" + clazz.getName() + "\"");
        }
        LuaUserdata result = new LuaUserdata(obj);
        result.setmetatable(metatable);
        return result;
    }

    public record FirstSeparatedArray<V>(V first, V[] rest) {}
}
