package com.github.applejuiceyy.automa.client.lua.boundary;

import com.github.applejuiceyy.automa.client.lua.annotation.IsIndex;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.*;
import org.spongepowered.include.com.google.common.base.Function;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class ParameterTree {
    HashMap<String, List<ParameterTree>> nodes;
    @Nullable CollectedMethod executes;

    ParameterTree(HashMap<String, List<ParameterTree>> nodes, @Nullable CollectedMethod executes) {
        this.nodes = nodes;
        this.executes = executes;
    }

    Object execute(Varargs vars) {
        ArrayList<Function<Annotation[], Object>> args = new ArrayList<>(vars.narg());
        for (int i = 0; i < vars.narg(); i++) {
            args.add(null);
        }
        Optional<CompletedTraverse> transversing = traverse(vars, args, 0);

        if (transversing.isPresent()) {
            CompletedTraverse traversion = transversing.get();
            Method method = traversion.invoked;

            if (method.isAnnotationPresent(IsIndex.class)) {
                if (traversion.result instanceof Integer integer) {
                    return integer + 1;
                }
                else {
                    throw new RuntimeException("parameter is annotated with IsIndex yet doesn't return an integer");
                }
            }
            return transversing.get().result;
        } else {
            throw new LuaError("Invalid Overload");
        }
    }

    Optional<CompletedTraverse> traverse(Varargs vars, ArrayList<Function<Annotation[], Object>> args, int position) {
        if (vars.narg() == position) {
            if (executes == null) {
                return Optional.empty();
            }

            Object[] complied = new Object[args.size()];

            for(int i = 0; i < complied.length; i++) {
                complied[i] = args.get(i).apply(executes.annotations[i]);
            }

            try {
                if (Modifier.isStatic(executes.method.getModifiers())) {
                    return Optional.of(new CompletedTraverse(executes.method.invoke(null, args), executes.method));
                }
                else {
                    Object first = complied[0];
                    Object[] shift = new Object[position - 1];
                    System.arraycopy(complied, 1, shift, 0, position - 1);
                    return Optional.of(new CompletedTraverse(executes.method.invoke(first, shift), executes.method));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Illegal access");
            } catch (InvocationTargetException e) {
                throw new LuaError(e.getTargetException());
            }
        }

        for (Map.Entry<String, List<ParameterTree>> entry : nodes.entrySet()) {
            LuaValue value = vars.arg(position + 1);
            String name = entry.getKey();
            Object arg = switch (name) {
                case "java.lang.Number", "java.lang.Double", "double" -> value.isnumber() ? value.checkdouble() : null;
                case "java.lang.String" -> value.isstring() ? value.checkjstring() : null;
                case "java.lang.Boolean", "boolean" -> value.isboolean() ? value.checkboolean() : null;
                case "java.lang.Float", "float" -> value.isnumber() ? (float) value.checkdouble() : null;
                case "java.lang.Integer", "int" -> value.isnumber() ? value.checkint() : null;
                case "java.lang.Long", "long" -> value.islong() ? value.checklong() : null;
                case "org.luaj.vm2.LuaTable" -> value.istable() ? value.checktable() : null;
                case "org.luaj.vm2.LuaFunction" -> value.isfunction() ? value.checkfunction() : null;
                case "org.luaj.vm2.LuaValue" -> value;
                default -> {
                    Object val = value.checkuserdata();
                    try {
                        if (Class.forName(name).isAssignableFrom(val.getClass())) {
                            yield val;
                        }
                        else {
                            yield null;
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Class " + name + " not found");
                    }
                }
            };

            if (arg != null) {
                for (ParameterTree nodeInfo : entry.getValue()) {
                    args.set(position, (annotation) -> {
                        assert annotation != null;
                        if (Arrays.stream(annotation).anyMatch(IsIndex.class::isInstance)) {
                            if (arg instanceof Integer integer) {
                                return integer - 1;
                            }
                            else {
                                throw new RuntimeException("parameter is annotated with IsIndex yet doesn't have an integer");
                            }
                        }
                        else {
                            return arg;
                        }
                    });

                    return nodeInfo.traverse(vars, args, position + 1);
                }
            }
        }

        return Optional.empty();
    }


    public static ParameterTree from(Method[] methods) {
        return from(Arrays.stream(methods).map(CollectedMethod::from).toList(), 0);
    }

    public static ParameterTree from(List<CollectedMethod> methods, int padding) {
        HashMap<String, ArrayList<CollectedMethod>> sorted = new HashMap<>();
        CollectedMethod executes = null;

        for (CollectedMethod method: methods) {
            if (method.parameters.length == padding) {
                executes = method;
            }
            else {
                String type = method.parameters[padding].getTypeName();

                if (!sorted.containsKey(type)) {
                    sorted.put(type, new ArrayList<>());
                }

                sorted.get(type).add(method);
            }
        }

        HashMap<String, List<ParameterTree>> assembled = new HashMap<>();

        for (Map.Entry<String, ArrayList<CollectedMethod>> entry: sorted.entrySet()) {
            List<ParameterTree> f = new ArrayList<>();
            assembled.put(entry.getKey(), f);

            f.add(from(entry.getValue(), padding + 1));
        }

        return new ParameterTree(assembled, executes);
    }

    record CollectedMethod(Method method, Type[] parameters, Annotation[][] annotations) {
        static HashMap<Method, CollectedMethod> cache = new HashMap<>();

        static CollectedMethod from(Method method) {
            if (cache.containsKey(method)) {
                return cache.get(method);
            }

            Type[] parameters = method.getGenericParameterTypes();
            Annotation[][] annotations = method.getParameterAnnotations();

            if (!Modifier.isStatic(method.getModifiers())) {
                Type[] n = new Type[parameters.length + 1];
                System.arraycopy(parameters, 0, n, 1, parameters.length);
                n[0] = new Type() {
                    @Override
                    public String getTypeName() {
                        return method.getDeclaringClass().getCanonicalName();
                    }
                };
                parameters = n;
                Annotation[][] an = new Annotation[annotations.length + 1][];
                System.arraycopy(annotations, 0, an, 1, annotations.length);
                an[0] = new Annotation[0];
                annotations = an;
            }


            CollectedMethod m = new CollectedMethod(method, parameters, annotations);
            cache.put(method, m);
            return m;
        }
    }

    record CompletedTraverse(Object result, Method invoked) {}
}
