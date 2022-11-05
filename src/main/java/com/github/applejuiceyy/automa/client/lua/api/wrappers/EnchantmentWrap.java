package com.github.applejuiceyy.automa.client.lua.api.wrappers;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;


@LuaConvertible
public record EnchantmentWrap(Enchantment enchantment) implements Wrapper<Enchantment> {
    @LuaConvertible
    public String id() {
        return Registry.ENCHANTMENT.getKey(enchantment).map((key) -> key.getValue().toString()).orElse(null);
    }

    @Override
    public Enchantment getWrapped() {
        return enchantment;
    }
}
