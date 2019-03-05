package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.fantasticsource.mctools.MCTools.*;
import static com.fantasticsource.tools.Tools.*;

@SideOnly(Side.CLIENT)
public class Render
{
    private static Field activeRenderInfoViewportField, activeRenderInfoProjectionField, activeRenderInfoModelviewField, minecraftRenderPartialTicksPausedField;

    private static float fov, fovMultiplier;


    public static void init()
    {
        try
        {
            activeRenderInfoViewportField = ReflectionTool.getField(ActiveRenderInfo.class, "field_178814_a", "VIEWPORT");
            activeRenderInfoProjectionField = ReflectionTool.getField(ActiveRenderInfo.class, "field_178813_c", "PROJECTION");
            activeRenderInfoModelviewField = ReflectionTool.getField(ActiveRenderInfo.class, "field_178812_b", "MODELVIEW");
            minecraftRenderPartialTicksPausedField = ReflectionTool.getField(Minecraft.class, "field_193996_ah", "renderPartialTicksPaused");

            MinecraftForge.EVENT_BUS.register(Render.class);
        }
        catch (Exception e)
        {
            crash(e, 701, false);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void updateFOV(EntityViewRenderEvent.FOVModifier event)
    {
        fov = event.getFOV();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void updateFOVMultiplier(FOVUpdateEvent event)
    {
        fovMultiplier = event.getNewfov();
    }


    public static float getVFOV()
    {
        //This should already be accounting for partialticks behind the scenes
        return fov * fovMultiplier;
    }

    public static double getHFOV(TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        return radtodeg(trigLookupTable.arctan(getZNearWidth() * 0.5 / getZNearDist())) * 2;
    }


    public static double getPartialTick() throws IllegalAccessException
    {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.isGamePaused() ? (double) (float) minecraftRenderPartialTicksPausedField.get(mc) : mc.getRenderPartialTicks();
    }


    public static Pair<Float, Float> getEntityXYInWindow(Entity entity, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        return getEntityXYInWindow(entity, 0, 0, 0, trigLookupTable);
    }

    public static Pair<Float, Float> getEntityXYInWindow(Entity entity, double xOffset, double yOffset, double zOffset, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        double partialTick = getPartialTick();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick + xOffset;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick + yOffset;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick + zOffset;

        return get2DWindowCoordsFrom3DWorldCoords(x, y, z, partialTick, trigLookupTable);
    }

    public static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        return get2DWindowCoordsFrom3DWorldCoords(x, y, z, getPartialTick(), trigLookupTable);
    }

    /**
     * When the entity is visible in the current projection, the returned values are its position in the window
     * When the entity is not visible in the current projection, the returned values are an off-screen position with the correct ratio to be used for an edge-of-screen indicator
     */
    private static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z, double partialTick, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        Vec3d cameraPos = getCameraPosition();

        double yawDif = posMod(angleDifDeg(manager.playerViewY, getYawDeg(cameraPos, new Vec3d(x, y, z), trigLookupTable)), 360);
        double pitchDif = posMod(angleDifDeg(manager.playerViewX, getPitchDeg(cameraPos, new Vec3d(x, y, z), trigLookupTable)), 360);
        if (yawDif >= 180) yawDif -= 360;
        if (pitchDif >= 180) pitchDif -= 360;

        double zNear = getZNearDist();
        double xFactor = yawDif <= -90 ? Double.NEGATIVE_INFINITY : yawDif >= 90 ? Double.POSITIVE_INFINITY : zNear * trigLookupTable.tan(degtorad(yawDif)) / getZNearWidth();
        double yFactor = pitchDif <= -90 ? Double.NEGATIVE_INFINITY : pitchDif >= 90 ? Double.POSITIVE_INFINITY : zNear * trigLookupTable.tan(degtorad(pitchDif)) / getZNearHeight();

        return new Pair<>((float) (0.5 + xFactor) * getViewportWidth(), (float) (0.5 + yFactor) * getViewportHeight());
    }


    public static double getZNearDist() throws IllegalAccessException
    {
        FloatBuffer projection = (FloatBuffer) activeRenderInfoProjectionField.get(null);
        return (2f * projection.get(11)) / (2f * projection.get(10) - 2f);
    }

    public static double getZNearWidth() throws IllegalAccessException
    {
        return getZNearDist() * 2 / ((FloatBuffer) activeRenderInfoProjectionField.get(null)).get(0);
    }

    public static double getZNearHeight() throws IllegalAccessException
    {
        return getZNearDist() * 2 / ((FloatBuffer) activeRenderInfoProjectionField.get(null)).get(5);
    }


    /**
     * This is not the width of the near plane!  This is the PORT width, not the VIEW width, ie. usually the window width
     */
    public static int getViewportWidth() throws IllegalAccessException
    {
        return ((IntBuffer) activeRenderInfoViewportField.get(null)).get(2);
    }

    /**
     * This is not the height of the near plane!  This is the PORT height, not the VIEW height, ie. usually the window height
     */
    public static int getViewportHeight() throws IllegalAccessException
    {
        return ((IntBuffer) activeRenderInfoViewportField.get(null)).get(3);
    }


    public static Vec3d getCameraPosition()
    {
        return Minecraft.getMinecraft().player.getPositionVector().add(ActiveRenderInfo.getCameraPosition());
    }
}
