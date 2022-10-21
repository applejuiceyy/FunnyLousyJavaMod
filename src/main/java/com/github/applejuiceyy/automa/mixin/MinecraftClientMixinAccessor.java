package com.github.applejuiceyy.automa.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientMixinAccessor {
    @Invoker
    boolean invokeDoAttack();
    @Invoker
    void invokeDoItemUse();
    @Invoker
    void invokeDoItemPick();
    @Invoker
    void invokeHandleBlockBreaking(boolean breaking);

    @Accessor("itemUseCooldown")
    int getItemUseCooldown();
}
