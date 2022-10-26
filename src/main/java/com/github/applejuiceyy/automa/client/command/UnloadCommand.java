package com.github.applejuiceyy.automa.client.command;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class UnloadCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> create() {
        ArgumentBuilder<FabricClientCommandSource, ?> literal = LiteralArgumentBuilder.literal("unload");
        literal.executes(context -> {
            LuaExecutionContainer.stopExecutor();
            return 1;
        });
        return literal;
    }
}
