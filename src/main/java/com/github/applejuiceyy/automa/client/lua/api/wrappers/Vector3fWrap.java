package com.github.applejuiceyy.automa.client.lua.api.wrappers;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Metatable;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import com.github.applejuiceyy.automa.client.lua.api.Wrapper;
import org.joml.Vector3f;

@LuaConvertible
public record Vector3fWrap(Vector3f vector) implements Wrapper<Vector3f> {
    @Property
    public float x() {return vector.x; }
    @Property
    public float y() {return vector.y; }
    @Property
    public float z() {return vector.z; }

    @Property.Setter
    public void x(float v) { vector.x = v; }
    @Property.Setter
    public void y(float v) { vector.y = v; }
    @Property.Setter
    public void z(float v) { vector.z = v; }

    @LuaConvertible
    public Vector3f floor() {
        return vector.floor(new Vector3f());
    }

    @LuaConvertible
    public Vector3f ceil() {
        return vector.ceil(new Vector3f());
    }

    @LuaConvertible
    public float length() {
        return vector.length();
    }

    @Metatable
    public Vector3f __add(Vector3f other) {
        return vector.add(other, new Vector3f(0, 0, 0));
    }

    @Metatable
    public Vector3f __sub(Vector3f other) {
        return vector.sub(other, new Vector3f(0, 0, 0));
    }

    @Metatable
    public Vector3f __div(Vector3f other) {
        return vector.div(other, new Vector3f(0, 0, 0));
    }

    @Metatable
    public Vector3f __mul(Vector3f other) {
        return vector.mul(other, new Vector3f(0, 0, 0));
    }

    @Metatable
    public boolean __eq(Vector3f other) {
        return vector.equals(other);
    }

    @Override
    public Vector3f getWrapped() {
        return vector;
    }
}
