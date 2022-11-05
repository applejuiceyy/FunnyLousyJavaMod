package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import com.github.applejuiceyy.automa.mixin.screenhandler.EnchantmentScreenHandlerAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class AutomatedEnchantmentTable extends AutomatedScreenHandler<EnchantmentScreenHandler> {
    AutomatedEnchantmentTable(LuaExecution executor, ScreenHandler handler) {
        super(executor, (EnchantmentScreenHandler) handler);
    }

    @Property
    public DynamicSlotReference enchantment() {
        return new DynamicSlotReference(this, ((EnchantmentScreenHandlerAccessor) handler).getInventory(), 0);
    }

    @Property
    public DynamicSlotReference payment() {
        return new DynamicSlotReference(this, ((EnchantmentScreenHandlerAccessor) handler).getInventory(), 1);
    }

    @LuaConvertible
    public EnchantmentEntryInfo getEnchantment(@IsIndex int index) {
        if(handler.enchantmentId[index] > 0) {
            return new EnchantmentEntryInfo(Enchantment.byRawId(handler.enchantmentId[index]), handler.enchantmentLevel[index], handler.enchantmentPower[index], index);
        }

        return null;
    }

    @LuaConvertible
    public record EnchantmentEntryInfo(Enchantment enchantment, int level, int power, int pos) {
        @LuaConvertible
        public boolean affordable() {
            return getPlayer().experienceLevel >= power && getPlayer().experienceLevel >= (pos + 1);
        }

        @LuaConvertible
        public int getRequiredLevel() {
            return Math.max(power, pos + 1);
        }

        @LuaConvertible
        public int getCost() {
            return pos + 1;
        }

        @LuaConvertible
        public Enchantment getEnchantmentClue() {
            return enchantment;
        }

        @LuaConvertible
        public Enchantment getEnchantment() {
            return enchantment;
        }

        @LuaConvertible
        public int getLevel() {
            return power;
        }
    }
}
