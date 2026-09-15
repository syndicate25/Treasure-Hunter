package com.treasurehunter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "treasurehunter", name = "Treasure Hunter", version = "1.0.0")
public class TreasureHunterMod {

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Hooks into the Forge event system to listen for map usage
        MinecraftForge.EVENT_BUS.register(new TreasureMapHandler());
    }
}