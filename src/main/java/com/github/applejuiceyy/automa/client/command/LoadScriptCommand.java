package com.github.applejuiceyy.automa.client.command;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.sun.jdi.connect.Connector;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.github.applejuiceyy.automa.client.AutomaClient.getScriptsPath;

public class LoadScriptCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> create() {
        ArgumentBuilder<FabricClientCommandSource, ?> literal = LiteralArgumentBuilder.literal("load");
        RequiredArgumentBuilder<FabricClientCommandSource, ?> sub = RequiredArgumentBuilder.argument("name", StringArgumentType.string());
        sub.executes(context -> {
            LuaExecutionContainer.setExecutor(
                    new LuaExecutionFacade(getScriptsPath().resolve(context.getArgument("name", String.class)))
            );
            return 1;
        });
        return literal.then(sub);
    }
}