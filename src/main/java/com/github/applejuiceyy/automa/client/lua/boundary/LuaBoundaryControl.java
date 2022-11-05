package com.github.applejuiceyy.automa.client.lua.boundary;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotAction;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.InventoryAccess;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Metatable;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import com.github.applejuiceyy.automa.client.lua.api.Player;
import com.github.applejuiceyy.automa.client.lua.api.ScreenAPI;
import com.github.applejuiceyy.automa.client.lua.api.World;
import com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls.InventoryControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.lookControls.LookControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.movementControls.MovementControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.listener.CancellationState;
import com.github.applejuiceyy.automa.client.lua.api.listener.Event;
import com.github.applejuiceyy.automa.client.lua.api.listener.Future;
import com.github.applejuiceyy.automa.client.lua.api.wrappers.*;
import com.github.applejuiceyy.automa.client.lua.entrypoint.AutomationEntrypoint;
import com.github.applejuiceyy.automa.client.automatedscreenhandler.*;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.jetbrains.annotations.Contract;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.*;
import java.util.*;

import static com.github.applejuiceyy.automa.client.lua.boundary.LuaWrappingHelper.ensureUnwrapped;
import static com.github.applejuiceyy.automa.client.lua.boundary.LuaWrappingHelper.ensureWrapped;

public class LuaBoundaryControl {
    private final HashMap<Class<?>, LuaTable> cache = new HashMap<>();
    private final LuaExecution owner;

    static Class<?>[] loadClasses = {
            Event.class,
            Future.class,

            MovementControlsLA.class,
            InventoryControlsLA.class,
            LookControlsLA.class,

            ScreenAPI.class,
            AutomatedPlayerScreen.class,
            AutomatedAnvil.class,
            AutomatedBeacon.class,
            AutomatedBrewingStand.class,
            AutomatedCrafting.class,
            AutomatedEnchantmentTable.class,
            AutomatedEnchantmentTable.EnchantmentEntryInfo.class,
            AutomatedFurnace.class,
            AutomatedGeneric.class,
            AutomatedLectern.class,
            Generic2ItemMerger.class,

            AutomatedScreenHandler.class,
            DynamicSlotReference.class,
            DynamicSlotAction.class,
            InventoryAccess.class,

            CancellationState.class,

            Player.class,

            ItemStackWrap.class,
            World.class,
            BlockWrap.class,
            BlockStateWrap.class,
            ItemWrap.class,
            EnchantmentWrap.class,
            Vector3fWrap.class
    };

    public LuaBoundaryControl(LuaExecution owner) {
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


        LuaTable metatable = new LuaTable();

        HashMap<String, ArrayList<Method>> methods = new HashMap<>();
        HashMap<String, Method> getters = new HashMap<>();
        HashMap<String, Method> setters = new HashMap<>();

        for(Method method: cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Property.class)) {
                assertPublic(method, "Method with Property must be public");

                getters.put(method.getName(), method);
            }
            else if (method.isAnnotationPresent(Property.Setter.class)) {
                assertPublic(method, "Method %s from %s with Property Setter must be public");

                setters.put(method.getName(), method);
            }
            else if (method.isAnnotationPresent(Metatable.class)) {
                assertPublic(method, "Method %s from %s with Metatable must be public");

                if(Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("Method " + method.getName() + " must not be static");
                }

                metatable.set(method.getName(), new MethodWrapper(this, new Method[]{method}, false));
            }
            else if(method.isAnnotationPresent(LuaConvertible.class)) {
                assertPublic(method, "Method %s from %s with LuaConvertible must be public");

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

        PropertyManager properties = PropertyManager.from(this, cls.getSimpleName(), getters, setters, methods);

        Class<?> supercls = cls.getSuperclass();

        if (supercls != Object.class && supercls != Record.class) {
            if (supercls.isAnnotationPresent(LuaConvertible.class)) {
                LuaTable supermeta = buildClass(supercls);
                for(LuaValue v: supermeta.keys()) {
                    if (metatable.get(v) == LuaValue.NIL) {
                        metatable.set(v, supermeta.get(v));
                    }
                }
                metatable.set("__index", properties.getGetter(supermeta));
            } else {
                throw new RuntimeException("A class annotated with LuaConvertible should inherit a class annotated with LuaConvertible (tested " + supercls.getName() + ")");
            }
        }
        else {
            metatable.set("__index", properties.getGetter());
        }
        metatable.set("__newindex", properties.getSetter());

        cache.put(cls, metatable);

        return metatable;
    }

    public Object L2J(LuaValue val) {
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
            return ensureUnwrapped(obj);
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

        val = ensureWrapped(val);

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

    static void assertPublic(Method method, String message) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new RuntimeException(String.format(message, method.getName(), method.getDeclaringClass().getName()));
        }
    }
}
