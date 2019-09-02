package com.fantasticsource.mctools.gui.guielements.rect.text.filter;

public class FilterNone extends TextFilter
{
    public static final FilterNone INSTANCE = new FilterNone();

    private FilterNone()
    {
    }

    @Override
    public boolean acceptable(String input)
    {
        return true;
    }
}