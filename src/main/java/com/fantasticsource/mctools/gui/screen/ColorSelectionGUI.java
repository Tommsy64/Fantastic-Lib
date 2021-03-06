package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class ColorSelectionGUI extends GUIScreen
{
    public ColorSelectionGUI(GUIColor clickedElement)
    {
        this(clickedElement, 1);
    }

    public ColorSelectionGUI(GUIColor clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        Color color = clickedElement.getValue();


        //Setup
        GUIView left = new GUIView(this, 0.3, 1);
        GUIView center = new GUIView(this, 0.3, 1);
        GUIView right = new GUIView(this, 0.3, 1);

        FilterRangedInt filter0to255 = FilterRangedInt.get(0, 255);
        FilterRangedFloat filter0to1 = FilterRangedFloat.get(0, 1);

        GUILabeledTextInput
                hex = new GUILabeledTextInput(this, "Hex: ", color.hex8(), FilterColor.INSTANCE),
                dec = new GUILabeledTextInput(this, "Dec: ", "" + color.color(), FilterInt.INSTANCE),
                r = new GUILabeledTextInput(this, "Red (0-255): ", "" + color.r(), filter0to255),
                g = new GUILabeledTextInput(this, "Green (0-255): ", "" + color.g(), filter0to255),
                b = new GUILabeledTextInput(this, "Blue (0-255): ", "" + color.b(), filter0to255),
                a = new GUILabeledTextInput(this, "Alpha (0-255): ", "" + color.a(), filter0to255),
                rf = new GUILabeledTextInput(this, "Red (0-1): ", "" + color.rf(), filter0to1),
                gf = new GUILabeledTextInput(this, "Green (0-1): ", "" + color.gf(), filter0to1),
                bf = new GUILabeledTextInput(this, "Blue (0-1): ", "" + color.bf(), filter0to1),
                af = new GUILabeledTextInput(this, "Alpha (0-1): ", "" + color.af(), filter0to1);

        GUIGradient preview = new GUIGradient(this, 1, 0.3, color);

        GUITextButton
                save = new GUITextButton(this, "Save", Color.GREEN),
                cancel = new GUITextButton(this, "Cancel", Color.RED);


        //Root
        root.add(new GUIDarkenedBackground(this));
        root.add(new GUINavbar(this));

        root.add(save.addClickActions(() ->
        {
            clickedElement.setValue(color);
            close();
        }));
        root.add(cancel.addClickActions(this::close));
        root.add(new GUIGradientBorder(this, 1, 0.01, 1, Color.GRAY, Color.BLANK));

        root.addAll(new GUIElement(this, 0.025, 1), left, new GUIElement(this, 0.025, 1), center, new GUIElement(this, 0.025, 1), right);


        //Left
        left.add(new GUITextSpacer(this));
        left.add(r);
        r.input.addEditActions(() ->
        {
            if (r.input.isActive() && r.input.valid())
            {
                color.setR(filter0to255.parse(r.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                rf.setInput("" + color.rf());
            }
        });
        left.add(new GUIElement(this, 1, 0));
        left.add(g);
        g.input.addEditActions(() ->
        {
            if (g.input.isActive() && g.input.valid())
            {
                color.setG(filter0to255.parse(g.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                gf.setInput("" + color.gf());
            }
        });
        left.add(new GUIElement(this, 1, 0));
        left.add(b);
        b.input.addEditActions(() ->
        {
            if (b.input.isActive() && b.input.valid())
            {
                color.setB(filter0to255.parse(b.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                bf.setInput("" + color.bf());
            }
        });
        left.add(new GUIElement(this, 1, 0));
        left.add(a);
        a.input.addEditActions(() ->
        {
            if (a.input.isActive() && a.input.valid())
            {
                color.setA(filter0to255.parse(a.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                af.setInput("" + color.af());
            }
        });


        //Center
        center.add(new GUITextSpacer(this));
        center.add(hex);
        hex.input.addEditActions(() ->
        {
            if (hex.input.isActive() && hex.input.valid())
            {
                color.setColor(FilterColor.INSTANCE.parse(hex.input.getText()));
                preview.setColor(color);
                dec.setInput("" + color.color());
                r.setInput("" + color.r());
                g.setInput("" + color.g());
                b.setInput("" + color.b());
                a.setInput("" + color.a());
                rf.setInput("" + color.rf());
                gf.setInput("" + color.gf());
                bf.setInput("" + color.bf());
                af.setInput("" + color.af());
            }
        });
        center.add(new GUITextSpacer(this));
        center.add(dec);
        dec.input.addEditActions(() ->
        {
            if (dec.input.isActive() && dec.input.valid())
            {
                color.setColor(FilterInt.INSTANCE.parse(dec.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                r.setInput("" + color.r());
                g.setInput("" + color.g());
                b.setInput("" + color.b());
                a.setInput("" + color.a());
                rf.setInput("" + color.rf());
                gf.setInput("" + color.gf());
                bf.setInput("" + color.bf());
                af.setInput("" + color.af());
            }
        });
        center.add(new GUITextSpacer(this));
        center.add(preview);


        //Right
        right.add(new GUITextSpacer(this));
        right.add(rf);
        rf.input.addEditActions(() ->
        {
            if (rf.input.isActive() && rf.input.valid())
            {
                color.setRF(filter0to1.parse(rf.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                r.setInput("" + color.r());
            }
        });
        right.add(new GUIElement(this, 1, 0));
        right.add(gf);
        gf.input.addEditActions(() ->
        {
            if (gf.input.isActive() && gf.input.valid())
            {
                color.setGF(filter0to1.parse(gf.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                g.setInput("" + color.g());
            }
        });
        right.add(new GUIElement(this, 1, 0));
        right.add(bf);
        bf.input.addEditActions(() ->
        {
            if (bf.input.isActive() && bf.input.valid())
            {
                color.setBF(filter0to1.parse(bf.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                b.setInput("" + color.b());
            }
        });
        right.add(new GUIElement(this, 1, 0));
        right.add(af);
        af.input.addEditActions(() ->
        {
            if (af.input.isActive() && af.input.valid())
            {
                color.setAF(filter0to1.parse(af.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8());
                dec.setInput("" + color.color());
                a.setInput("" + color.a());
            }
        });
    }

    @Override
    public String title()
    {
        return "Color";
    }
}
