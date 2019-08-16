package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData
{
    public static LinkedHashMap<UUID, PlayerData> playerData = new LinkedHashMap<>();

    private static String modDir = Loader.instance().getConfigDir().getAbsolutePath() + File.separator + FantasticLib.MODID + File.separator;
    private static String referenceDir = modDir + "reference" + File.separator;

    public String name;
    public UUID id;
    public EntityPlayer player;

    public PlayerData(String name, UUID id)
    {
        this(name, id, null);
    }

    public PlayerData(String name, UUID id, EntityPlayer player)
    {
        this.name = name;
        this.id = id;
        this.player = player;
    }


    public static PlayerData get(UUID id)
    {
        PlayerData result = playerData.get(id);
        if (result != null)
        {
            if (result.player != null && !result.player.getName().equals(result.name)) result.name = result.player.getName();
            return result;
        }

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return null;

        PlayerList playerList = server.getPlayerList();
        if (playerList == null) return null;

        EntityPlayer player = playerList.getPlayerByUUID(id);
        if (player == null) return null; //getPlayerByUUID() can absolutely return null

        result = new PlayerData(player.getName(), player.getPersistentID(), player);
        playerData.put(id, result);
        return result;
    }

    public static PlayerData get(String name)
    {
        PlayerData result;
        for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet())
        {
            result = entry.getValue();
            if (result.name.equals(name)) return result;
        }

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return null;

        PlayerList playerList = server.getPlayerList();
        if (playerList == null) return null;

        EntityPlayer player = playerList.getPlayerByUsername(name);
        if (player == null) return null;

        result = new PlayerData(player.getName(), player.getPersistentID(), player);
        playerData.put(player.getPersistentID(), result);
        return result;
    }

    public static String getName(UUID id)
    {
        PlayerData data = get(id);
        return data == null ? null : data.name;
    }

    public static UUID getID(String name)
    {
        PlayerData data = get(name);
        return data == null ? null : data.id;
    }


    public static void save()
    {
        File file = new File(modDir);
        if (!file.exists()) file.mkdir();

        file = new File(referenceDir);
        if (!file.exists()) file.mkdir();

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(referenceDir + "players.txt")));
            for (UUID id : playerData.keySet())
            {
                PlayerData data = get(id);
                if (data != null && data.name != null) writer.write(id + " = " + data.name + "\r\n");
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void load()
    {
        File file = new File(modDir);
        if (!file.exists()) file.mkdir();

        file = new File(referenceDir);
        if (!file.exists()) file.mkdir();

        file = new File(referenceDir + "players.txt");
        if (!file.exists())
        {
            save();
            return;
        }

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null && !line.trim().equals(""))
            {
                String[] tokens = line.split("=");
                UUID id = UUID.fromString(tokens[0].trim());
                playerData.put(id, new PlayerData(tokens[1].trim(), id));

                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @SubscribeEvent
    public static void playerLogon(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            playerData.put(player.getPersistentID(), new PlayerData(player.getName(), player.getPersistentID(), player));
            save();
        }
    }

    @SubscribeEvent
    public static void playerLogoff(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player != null)
        {
            UUID id = event.player.getPersistentID();
            if (playerData.containsKey(id))
            {
                playerData.get(id).player = null;
            }
        }
    }
}
