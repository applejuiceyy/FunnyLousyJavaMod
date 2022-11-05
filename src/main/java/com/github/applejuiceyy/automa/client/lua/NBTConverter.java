package com.github.applejuiceyy.automa.client.lua;

import net.minecraft.nbt.*;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.function.Function;

public class NBTConverter {
    private static final HashMap<Class<?>, Function<NbtElement, LuaValue>> CONVERTERS = new HashMap<>() {{
        //primitive types
        put(NbtByte.class, tag -> LuaValue.valueOf(((NbtByte) tag).byteValue()));
        put(NbtShort.class, tag -> LuaValue.valueOf(((NbtShort) tag).shortValue()));
        put(NbtInt.class, tag -> LuaValue.valueOf(((NbtInt) tag).intValue()));
        put(NbtLong.class, tag -> LuaValue.valueOf(((NbtLong) tag).longValue()));
        put(NbtFloat.class, tag -> LuaValue.valueOf(((NbtFloat) tag).floatValue()));
        put(NbtDouble.class, tag -> LuaValue.valueOf(((NbtDouble) tag).doubleValue()));

        //compound special :D
        put(NbtCompound.class, tag -> {
            LuaTable table = new LuaTable();
            NbtCompound compound = (NbtCompound) tag;

            for (String key : compound.getKeys())
                table.set(key, convert(compound.get(key)));

            return table;
        });

        //collection types
        put(NbtByteArray.class, tag -> fromCollection((AbstractNbtList<?>) tag));
        put(NbtIntArray.class, tag -> fromCollection((AbstractNbtList<?>) tag));
        put(NbtLongArray.class, tag -> fromCollection((AbstractNbtList<?>) tag));
        put(NbtList.class, tag -> fromCollection((AbstractNbtList<?>) tag));
    }};

    private static LuaValue fromCollection(AbstractNbtList<?> tag) {
        LuaTable table = new LuaTable();

        int i = 1;
        for (NbtElement children : tag) {
            table.set(i, convert(children));
            i++;
        }

        return table;
    }

    public static LuaValue convert(NbtElement tag) {
        if (tag == null)
            return null;

        Class<?> clazz = tag.getClass();
        Function<NbtElement, LuaValue> builder = CONVERTERS.get(clazz);
        if (builder == null)
            return LuaValue.valueOf(tag.asString());

        return builder.apply(tag);
    }
}
