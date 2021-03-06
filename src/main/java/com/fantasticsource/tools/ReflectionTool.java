package com.fantasticsource.tools;

import com.fantasticsource.mctools.MCTools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionTool
{
    public static Field getField(Class classType, String... possibleFieldnames)
    {
        return getField(false, classType, possibleFieldnames);
    }

    public static Field getField(boolean printFound, Class classType, String... possibleFieldnames)
    {
        try
        {
            Field[] fields = classType.getDeclaredFields();
            for (Field field : fields)
            {
                for (String name : possibleFieldnames)
                {
                    if (field.getName().equals(name))
                    {
                        field.setAccessible(true);

                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                        if (printFound) System.out.println(name);
                        return field;
                    }
                }
            }
        }
        catch (IllegalAccessException | NoSuchFieldException e)
        {
            MCTools.crash(e, 700, false);
        }

        return null;
    }


    public static Method getMethod(Class classType, String... possibleMethodNames)
    {
        return getMethod(false, classType, possibleMethodNames);
    }

    public static Method getMethod(boolean printFound, Class classType, String... possibleMethodNames)
    {
        Method[] methods = classType.getDeclaredMethods();
        for (Method method : methods)
        {
            for (String name : possibleMethodNames)
            {
                if (method.getName().equals(name))
                {
                    method.setAccessible(true);
                    if (printFound) System.out.println(name);
                    return method;
                }
            }
        }
        return null;
    }

    //Never change this method's name to getClass()
    public static Class getClassByName(String fullClassPathAndName)
    {
        try
        {
            return Class.forName(fullClassPathAndName);
        }
        catch (ClassNotFoundException e)
        {
            MCTools.crash(e, 701, false);
        }
        return null;
    }

    public static Class getInternalClass(Class classType, String... possibleInternalClassNames)
    {
        Class[] classes = classType.getDeclaredClasses();
        for (Class class1 : classes)
        {
            for (String name : possibleInternalClassNames)
            {
                if (class1.getSimpleName().equals(name))
                {
                    return class1;
                }
            }
        }
        return null;
    }


    public static void set(Class classType, String possibleFieldname, Object object, Object value)
    {
        set(classType, new String[]{possibleFieldname}, object, value);
    }

    public static void set(Class classType, String[] possibleFieldnames, Object object, Object value)
    {
        Field field = ReflectionTool.getField(classType, possibleFieldnames);
        if (field != null) set(field, object, value);
    }

    public static void set(Field field, Object object, Object value)
    {
        try
        {
            field.set(object, value);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 704, false);
        }
    }


    public static Object get(Class classType, String possibleFieldname, Object object)
    {
        return get(classType, new String[]{possibleFieldname}, object);
    }

    public static Object get(Class classType, String[] possibleFieldnames, Object object)
    {
        Field field = ReflectionTool.getField(classType, possibleFieldnames);
        return field == null ? null : get(field, object);
    }

    public static Object get(Field field, Object object)
    {
        try
        {
            return field.get(object);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 705, false);
            return null;
        }
    }


    public static Object invoke(Class classType, String possibleMethodName, Object object, Object... args)
    {
        return invoke(classType, new String[]{possibleMethodName}, object, args);
    }

    public static Object invoke(Class classType, String[] possibleMethodNames, Object object, Object... args)
    {
        Method method = getMethod(classType, possibleMethodNames);
        return method == null ? null : invoke(method, object, args);
    }

    public static Object invoke(Method method, Object object, Object... args)
    {
        try
        {
            return method.invoke(object, args);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            MCTools.crash(e, 706, false);
            return null;
        }
    }
}
