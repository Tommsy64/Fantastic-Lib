package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;

public class GUILabeledTextInput extends GUIAutocroppedView
{
    public final GUIText label;
    public final GUITextInput input;

    public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter)
    {
        this(screen, label, defaultInput, filter, 1);
    }

    public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter, double scale)
    {
        super(screen);

        input = new GUITextInput(screen, defaultInput, filter, scale);

        this.label = new GUIText(screen, label, scale);
        add(this.label.addClickActions(() ->
        {
            int length = input.text.length();
            input.cursorPosition = length;
            input.selectorPosition = length == 0 ? -1 : 0;

            input.setActive(true);
        }));

        add(input);
    }


    public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter)
    {
        this(screen, x, y, label, defaultInput, filter, 1);
    }

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter, double scale)
    {
        super(screen, x, y);

        input = new GUITextInput(screen, defaultInput, filter, scale);

        this.label = new GUIText(screen, label, scale);
        add(this.label.addClickActions(() ->
        {
            int length = input.text.length();
            input.cursorPosition = length;
            input.selectorPosition = length == 0 ? -1 : 0;

            input.setActive(true);
        }));

        add(input);
    }


    public GUILabeledTextInput setInput(String text)
    {
        input.setText(text);
        return this;
    }

    public GUILabeledTextInput setNamespace(String namespace)
    {
        input.setNamespace(namespace);
        return this;
    }


    @Override
    public void recalcAndRepositionSubElements(int startIndex)
    {
        super.recalcAndRepositionSubElements(startIndex);

        if (label != null && input != null)
        {
            label.recalc(0);
            input.x = label.width;
            input.y = 0;
        }
    }

    @Override
    public String toString()
    {
        return input.toString();
    }


    public boolean valid()
    {
        return input.valid();
    }

    public String getText()
    {
        return input.getText();
    }

    public void setText(String text)
    {
        input.setText(text);
    }

    @Override
    public GUILabeledTextInput addEditActions(Runnable... actions)
    {
        input.addEditActions(actions);

        return this;
    }
}
