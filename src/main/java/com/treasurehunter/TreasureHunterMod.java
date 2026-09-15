package com.treasurehunter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = "treasurehunter", name = "Treasure Hunter", version = "1.0.0")
public class TreasureHunterMod {

    public static String[] mapConfigurations;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        String[] defaults = {
            "quadrum:my_easy_map|recurrentcomplex:chests/easy_treasure|10",
            "quadrum:my_hard_map|lootpp:chests/hard_treasure|35"
        };

        mapConfigurations = config.getStringList("mapSettings", "Maps", defaults, 
            "Format: itemRegistryName|lootTableCommandKey|trapChancePercentage (Separate multiple maps with a new line)");

        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TreasureMapHandler());
    }
}