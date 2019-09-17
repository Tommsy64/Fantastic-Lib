package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GUIScrollView extends GUIView
{
    public double internalHeight, progress = -1;
    public double top, bottom;

    public GUIScrollView(GUIScreen screen, double width, double height, GUIElement... subElements)
    {
        super(screen, width, height);

        for (GUIElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc();
    }

    public GUIScrollView(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements)
    {
        super(screen, x, y, width, height);

        for (GUIElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc();
    }

    @Override
    public GUIElement recalc()
    {
        internalHeight = 0;
        super.recalc();
        for (GUIElement element : children)
        {
            internalHeight = Tools.max(internalHeight, (element.y + element.height) * getScreenHeight());
        }

        recalc2();

        return this;
    }

    protected void recalc2()
    {
        if (internalHeight <= height)
        {
            progress = -1;
            top = 0;
        }
        else
        {
            if (progress == -1) progress = 0;
            top = (internalHeight - height) * progress;
        }
        bottom = top + height;
    }

    @Override
    public void draw()
    {
        recalc2();

        if (children.size() > 0 && width > 0 && height > 0)
        {
            double screenWidth = screen.width, screenHeight = screen.height;

            int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
            double wScale = screenWidth * mcScale, hScale = screenHeight * mcScale;

            currentScissor = new int[]{(int) (getScreenX() * wScale), (int) ((1 - (getScreenY() + getScreenHeight())) * hScale), (int) (getScreenWidth() * wScale), (int) (getScreenHeight() * hScale)};
            if (parent != null && parent.currentScissor != null)
            {
                currentScissor[0] = Tools.max(currentScissor[0], parent.currentScissor[0]);
                currentScissor[1] = Tools.max(currentScissor[1], parent.currentScissor[1]);
                currentScissor[2] = Tools.min(currentScissor[2], parent.currentScissor[2]);
                currentScissor[3] = Tools.min(currentScissor[3], parent.currentScissor[3]);
            }

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(currentScissor[0], currentScissor[1], currentScissor[2], currentScissor[3]);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -top, 0);

            for (GUIElement element : children)
            {
                if (element.y * getScreenHeight() + element.height < top || element.y * getScreenHeight() >= bottom) continue;
                element.draw();
            }

            GlStateManager.popMatrix();

            currentScissor = null;
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        recalc2();
        y -= top;

        return super.mousePressed(x, y, button);
    }

    @Override
    public void mouseReleased(double x, double y, int button)
    {
        recalc2();
        y -= top;

        super.mouseReleased(x, y, button);
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        recalc2();
        y -= top;

        super.mouseDrag(x, y, button);
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        recalc2();
        y -= top;

        super.mouseWheel(x, y, delta);
    }

    @Override
    public double childMouseYOffset()
    {
        return top;
    }
}