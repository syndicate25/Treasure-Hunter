package com.treasurehunter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "treasurehunter", name = "Treasure Hunter", version = "1.0.0")
public class TreasureHunterMod {

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // Registers our custom command directly into the server's native command directory
        event.registerServerCommand(new CommandBuryTreasure());
    }
}
