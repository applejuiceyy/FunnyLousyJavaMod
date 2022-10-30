package com.github.applejuiceyy.automa.client.lua.boundary;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;

class MethodWrapper extends VarArgFunction {
    Method[] methods;
    LuaBoundaryControl controller;
    boolean isStatic;

    ParameterTree tree;

    MethodWrapper(LuaBoundaryControl owner, Method[] methods, boolean isStatic) {
        this.methods = methods;
        this.controller = owner;
        this.isStatic = isStatic;

        tree = ParameterTree.from(methods, isStatic);
    }

    @Override
    public Varargs invoke(Varargs args) {
        return this.controller.J2L(tree.execute(args));
    }
}