package com.treasurehunter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = "treasurehunter", name = "Treasure Hunter", version = "1.0.0")
public class TreasureHunterMod {

    // Added "public" to the front of both variables below:
    public static int easyTrapChance;
    public static int hardTrapChance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        easyTrapChance = config.getInt("easyTrapChance", "Traps", 10, 0, 100, "Percentage chance for an easy map to spawn a trapped chest (0-100)");
        hardTrapChance = config.getInt("hardTrapChance", "Traps", 35, 0, 100, "Percentage chance for a hard map to spawn a trapped chest (0-100)");

        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TreasureMapHandler());
    }
}
