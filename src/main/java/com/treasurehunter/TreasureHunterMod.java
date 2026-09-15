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
            "quadrum:my_easy_map|rc:darkCave|10",
            "quadrum:my_hard_map|vanilla:mineshaftCorridor|35"
        };

        String instructionBlock = "Define your custom map items and link them to their target loot profiles here.\n"
                + "Format: itemRegistryName|lootPrefix:lootKeyID|trapChancePercentage\n\n"
                + "LOOT PREFIX EXAMPLES:\n"
                + "  * Recurrent Complex (.rcig layout): Use \"rc:\" followed by the inventoryGeneratorID\n"
                + "    Example -> quadrum:dark_cave_map|rc:darkCave|20\n\n"
                + "  * Vanilla Minecraft Tables: Use \"vanilla:\" followed by the internal 1.7.10 generator key\n"
                + "    Example -> quadrum:mineshaft_map|vanilla:mineshaftCorridor|15\n"
                + "    (Common keys: mineshaftCorridor, dungeonChest, villageBlacksmith, strongholdLibrary, pyramidDeserChest)\n\n"
                + "  * Loot++ Tables: Use \"lpp:\" followed by your customized reward folder entry\n"
                + "    Example -> quadrum:custom_loot_map|lpp:chests/my_custom_table|5\n\n"
                + "Separate multiple map configuration lines onto a new line entry inside the collection bracket below.";

        mapConfigurations = config.getStringList("mapSettings", "Maps", defaults, instructionBlock);

        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TreasureMapHandler());
    }
}