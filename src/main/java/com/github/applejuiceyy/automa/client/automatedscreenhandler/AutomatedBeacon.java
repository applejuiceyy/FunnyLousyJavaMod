package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.mixin.screenhandler.BeaconScreenHandlerAccessor;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.luaj.vm2.LuaError;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getNetworkHandler;

@LuaConvertible
public class AutomatedBeacon extends AutomatedScreenHandler<BeaconScreenHandler> {
    AutomatedBeacon(LuaExecutionFacade executor, ScreenHandler handler) {
        super(executor, (BeaconScreenHandler) handler);
    }

    @LuaConvertible
    public int getLevel() {
        return handler.getProperties();
    }

    @LuaConvertible
    public DynamicSlotReference payment() {
        return new DynamicSlotReference(((BeaconScreenHandlerAccessor) handler).getPayment(), 0);
    }

    @LuaConvertible
    public boolean hasPayment() {
        return handler.hasPayment();
    }

    @LuaConvertible
    public StatusEffect currentPrimaryEffect() {
        return handler.getPrimaryEffect();
    }

    @LuaConvertible
    public StatusEffect currentSecondaryEffect() {
        return handler.getSecondaryEffect();
    }

    @LuaConvertible
    public void payWith(StatusEffect primaryEffect, boolean pickRegeneration) {
        Set<StatusEffect> effects = Arrays.stream(BeaconBlockEntity.EFFECTS_BY_LEVEL)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());

        StatusEffect secondary = pickRegeneration ? StatusEffects.REGENERATION : primaryEffect;

        if (effects.contains(primaryEffect)) {
            payEffects(primaryEffect, secondary);
        }
        else {
            throw new LuaError("Invalid effect");
        }
    }

    @LuaConvertible
    public void payWith(StatusEffect primaryEffect) {
        Set<StatusEffect> effects = Arrays.stream(BeaconBlockEntity.EFFECTS_BY_LEVEL)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());

        if (effects.contains(primaryEffect)) {
            payEffects(primaryEffect, null);
        }
        else {
            throw new LuaError("Invalid effect");
        }
    }

    private void payEffects(StatusEffect primaryEffect, StatusEffect secondaryEffect) {
        getNetworkHandler()
                .sendPacket(new UpdateBeaconC2SPacket(
                        Optional.ofNullable(primaryEffect),
                        Optional.ofNullable(secondaryEffect)
                        )
                );
    }
}
