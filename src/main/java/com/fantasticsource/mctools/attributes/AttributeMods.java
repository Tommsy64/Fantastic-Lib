package com.fantasticsource.mctools.attributes;

import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.UUID;

public class AttributeMods
{
    /**
     * Syntax is attribute = amount @ operation, attribute = amount @ operation, attribute = amount @ operation...
     */
    public static ArrayList<AttributeModifier> parseMods(String modList)
    {
        String[] modifiers = modList.split("&");
        for (int i = 0; i < modifiers.length; i++) modifiers[i] = modifiers[i].trim();
        return parseMods(modifiers);
    }

    /**
     * Syntax for each is attribute = amount @ operation
     */
    public static ArrayList<AttributeModifier> parseMods(String[] modList)
    {
        ArrayList<AttributeModifier> result = new ArrayList<>();

        AttributeModifier modifier;
        for (String string : modList)
        {
            modifier = parseMod(string);
            if (modifier != null) result.add(modifier);
        }

        return result;
    }

    /**
     * Syntax is attribute = amount @ operation
     */
    public static AttributeModifier parseMod(String modString)
    {
        String[] tokens = modString.split("=");
        if (tokens.length < 2 || tokens.length > 3)
        {
            System.err.println("Malformed attribute modifier string: " + modString);
            return null;
        }

        String[] tokens2 = tokens[1].split("@");
        if (tokens2.length > 2)
        {
            System.err.println("Malformed attribute modifier string: " + modString);
            return null;
        }

        return new AttributeModifier("FL_autogen_" + UUID.randomUUID().toString(), Double.parseDouble(tokens2[0]), tokens2.length > 1 ? Integer.parseInt(tokens2[1]) : 0);
    }
}