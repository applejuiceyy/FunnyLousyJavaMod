package com.github.applejuiceyy.automa.client.lua.boundary;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Player;
import com.github.applejuiceyy.automa.client.lua.api.ScreenAPI;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls.InventoryControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.lookControls.LookControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.movementControls.MovementControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.listener.CancellationState;
import com.github.applejuiceyy.automa.client.lua.api.listener.Event;
import com.github.applejuiceyy.automa.client.lua.api.listener.Future;
import com.github.applejuiceyy.automa.client.lua.api.world.*;
import com.github.applejuiceyy.automa.client.lua.entrypoint.AutomationEntrypoint;
import com.github.applejuiceyy.automa.client.screen_handler_interface.*;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.*;
import java.util.*;

public class LuaBoundaryControl {
    private final HashMap<Class<?>, LuaTable> cache = new HashMap<>();
    private final LuaExecutionFacade owner;

    static Class<?>[] loadClasses = {
            Event.class,
            Future.class,

            MovementControlsLA.class,
            InventoryControlsLA.class,
            LookControlsLA.class,

            ScreenAPI.class,
            AutomatedGeneric.class,
            AutomatedAnvil.class,
            AutomatedForging.class,
            AutomatedLectern.class,
            AutomatedScreenHandler.class,
            AutomatedScreenHandler.DynamicSlotReference.class,
            AutomatedScreenHandler.DynamicSlotAction.class,

            CancellationState.class,

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

        List<EntrypointContainer<AutomationEntrypoint>> entries =
                FabricLoaderImpl.INSTANCE.getEntrypointContainers("automation", AutomationEntrypoint.class);

        for (EntrypointContainer<AutomationEntrypoint> entry: entries){
            entry.getEntrypoint().loadClasses(this::buildClass);
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

        HashMap<String, ArrayList<Method>> methods = new HashMap<>();

        for(Method method: cls.getDeclaredMethods()) {
            if(method.isAnnotationPresent(LuaConvertible.class) && Modifier.isPublic(method.getModifiers())) {
                String name = method.getName();

                if (!methods.containsKey(name)) {
                    methods.put(name, new ArrayList<>());
                }

                ArrayList<Method> methods_ = methods.get(name);

                if(methods_.size() > 0) {
                    boolean should = Modifier.isStatic(methods_.get(methods_.size() - 1).getModifiers());
                    boolean isit = Modifier.isStatic(method.getModifiers());

                    if (should != isit) {
                        throw new RuntimeException("Overloads cannot vary in static");
                    }
                }

                methods_.add(method);
            }
        }

        for (Map.Entry<String, ArrayList<Method>> entry : methods.entrySet()) {
            Method[] arr = new Method[entry.getValue().size()];
            entry.getValue().toArray(arr);
            boolean isStatic = Modifier.isStatic(arr[0].getModifiers());
            indexing.set(entry.getKey(), new MethodWrapper(this, arr, isStatic));
        }

        indexing.set("new", constructor);

        for(Class<?> classes: cls.getDeclaredClasses()) {
            if (classes.isAnnotationPresent(LuaConvertible.class) && Modifier.isPublic(classes.getModifiers())) {
                indexing.set(classes.getName(), buildClass(classes));
            }
        }

        Class<?> supercls = cls.getSuperclass();

        if (supercls != Object.class && supercls != Record.class) {
            if (supercls.isAnnotationPresent(LuaConvertible.class)) {
                LuaTable supermeta = buildClass(supercls);
                metatable.setmetatable(supermeta);
                LuaTable indexMeta = new LuaTable();
                indexMeta.set("__index", supermeta.get("__index"));
                indexing.setmetatable(supermeta);
            } else {
                throw new RuntimeException("A class annotated with LuaConvertible should inherit a class annotated with LuaConvertible (tested " + supercls.getName() + ")");
            }
        }

        metatable.set("__index", indexing);

        cache.put(cls, metatable);

        return metatable;
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


    public Object[] LVarargs2J(Varargs args) {
        Object[] converted = new Object[args.narg()];

        for (int i = 0; i < args.narg(); i++) {
            converted[i] = L2J(args.arg(i + 1));
        }

        return converted;
    }

    public Varargs J2L(Object val) {
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

        if (val instanceof Varargs l)
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
}
