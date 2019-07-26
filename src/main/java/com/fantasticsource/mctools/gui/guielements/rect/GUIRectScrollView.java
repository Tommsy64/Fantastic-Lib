package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GUIRectScrollView extends GUIRectElement
{
    public double internalHeight, progress = -1;
    private GUIRectElement background;
    private double lastScreenWidth, lastScreenHeight, top, bottom;

    public GUIRectScrollView(GUIScreen screen, GUIRectElement background, double screenWidth, double screenHeight, GUIRectElement... subElements)
    {
        super(screen, background.x, background.y, background.width, background.height);

        this.background = background;
        for (GUIRectElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc(screenWidth, screenHeight);
    }

    public void recalc(double screenWidth, double screenHeight)
    {
        if (screenWidth == lastScreenWidth && screenHeight == lastScreenHeight) return;
        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;


        double pxWidth = screenWidth * width;
        internalHeight = 0;
        for (GUIElement element : children)
        {
            if (element instanceof GUIRectElement)
            {
                if (element instanceof GUITextRect) ((GUITextRect) element).recalcHeight(pxWidth, screenHeight);
                internalHeight = Tools.max(internalHeight, element.y + ((GUIRectElement) element).height);
            }
        }

        recalc2();
    }

    private void recalc2()
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
    public void draw(double screenWidth, double screenHeight)
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
        int scaledHeight = (int) (height * screenHeight * mcScale);
        GL11.glScissor((int) (x * screenWidth * mcScale), (int) (screenHeight * mcScale - y * screenHeight * (mcScale << 1)) - scaledHeight, (int) (width * screenWidth * mcScale), scaledHeight);
        GL11.glScissor((int) (x * screenWidth * mcScale), 0, (int) (width * screenWidth * mcScale), (int) (height * screenHeight * mcScale));
        recalc2();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -top, 0);

        for (GUIElement element : children)
        {
            if (element.y + ((GUIRectElement) element).height < top || element.y >= bottom) continue;
            element.draw(screenWidth, screenHeight);
        }

        GlStateManager.popMatrix();


        background.draw(screenWidth, screenHeight);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
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
