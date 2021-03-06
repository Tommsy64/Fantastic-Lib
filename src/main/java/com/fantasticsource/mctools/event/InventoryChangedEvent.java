package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.items.ItemMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InventoryChangedEvent extends EntityEvent
{
    public static LinkedHashMap<Entity, ArrayList<ItemStack>> previousContents = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(InventoryChangedEvent.class);
    }


    private final ArrayList<ItemStack> oldInventory;


    public InventoryChangedEvent(Entity entity, ArrayList<ItemStack> oldInventory)
    {
        super(entity);

        this.oldInventory = oldInventory;
    }

    public ArrayList<ItemStack> getOldInventory()
    {
        return new ArrayList<>(oldInventory);
    }


    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase == TickEvent.Phase.END) return;


        previousContents.entrySet().removeIf(entry ->
        {
            Entity entity = entry.getKey();
            if (!entity.isAddedToWorld())
            {
                return true;
            }

            for (World world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
            {
                if (world == event.world) return false;
            }

            return true;
        });

        ArrayList<ItemStack> oldInventory, newInventory;
        for (Entity entity : event.world.loadedEntityList.toArray(new Entity[0]))
        {
            if (!entity.isAddedToWorld())
            {
                previousContents.remove(entity);
                continue;
            }


            newInventory = GlobalInventory.getAllNonSkinItems(entity);
            oldInventory = previousContents.getOrDefault(entity, new ArrayList<>());
            if (newInventory.size() != oldInventory.size())
            {
                MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(entity, oldInventory));
                previousContents.put(entity, GlobalInventory.getAllNonSkinItems(entity));
                continue;
            }

            boolean match = true;
            for (int i = 0; i < newInventory.size(); i++)
            {
                if (!ItemMatcher.stacksMatch(oldInventory.get(i), newInventory.get(i)))
                {
                    match = false;
                    break;
                }
            }
            if (!match)
            {
                MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(entity, oldInventory));
                previousContents.put(entity, GlobalInventory.getAllNonSkinItems(entity));
            }
        }
    }
}
