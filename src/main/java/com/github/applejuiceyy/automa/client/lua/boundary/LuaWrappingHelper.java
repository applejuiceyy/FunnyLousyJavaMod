package com.github.applejuiceyy.automa.client.lua.boundary;

import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import com.github.applejuiceyy.automa.client.lua.api.world.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static com.github.applejuiceyy.automa.client.lua.LuaUtils.wrapException;

public class LuaWrappingHelper {
    static HashMap<Class<?>, Class<? extends Wrapper<?>>> wrappers = new HashMap<>(){{
        this.put(Block.class, BlockWrap.class);
        this.put(BlockState.class, BlockStateWrap.class);
        this.put(ItemStack.class, ItemStackWrap.class);
        this.put(Item.class, ItemWrap.class);
        this.put(Enchantment.class, EnchantmentWrap.class);
    }};

    static Object ensureWrapped(Object obj) {
        if (obj instanceof Wrapper<?>) {
            return obj;
        }

        Class<?> current = obj.getClass();

        while (current != Object.class) {
            if (wrappers.containsKey(current)) {
                Class<? extends Wrapper<?>> wrapping = wrappers.get(current);
                Constructor<?> constructor;
                try {
                    constructor = wrapping.getConstructor(current);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Wrapper class has no valid constructor");
                }

                try {
                    return constructor.newInstance(obj);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Attempt at instancing a wrapper instance failed");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Wrapper constructor is not accessible");
                } catch (InvocationTargetException e) {
                    throw wrapException(e.getTargetException());
                }
            }

            current = current.getSuperclass();
        }

        return obj;
    }


    static Object ensureUnwrapped(Object obj) {
        if (obj instanceof Wrapper<?> wrap) {
            return wrap.getWrapped();
        }

        return obj;
    }
}
