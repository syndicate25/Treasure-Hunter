package com.treasurehunter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import java.util.Random;

@Mod(modid = "treasurehunter", name = "Treasure Hunter", version = "1.0.0")
public class TreasureHunterMod extends CommandBase {

    public static ItemTreasureMap treasureMap;
    public static ItemTreasureMap treasureMap2;
    public static ItemTreasureMap treasureMap3;
    public static ItemTreasureMap treasureMap4;
    public static ItemTreasureMap treasureMap5;
    public static ItemTreasureMap treasureMap6;
    public static ItemTreasureMap treasureMap7;

    public static String[] masterLootPool;

    private final Random random = new Random();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        String[] defaultPool = {
            "mineshaftCorridor|70|2|4|5|6",
            "dungeonChest|30|4|8|15|10",
            "rc:custom_ruin_chest|15|6|5|25|12",
            "lpp:superrareLoot|1|12|7|65|20"
        };

        String comment = "MASTER LOOT POOL CONFIGURATION DATA\n"
                       + "Add entries using the following syntax structure:\n"
                       + "modded_loot_table_key | weight | min_bury | max_bury | trap_chance | fuzzy_variance\n\n"
                       + "NUMERIC RANGE AND PARAMETER LIMITS:\n"
                       + "  - modded_loot_table  : Internal loot string. Prefix with 'rc:' for Recurrent Complex, 'lpp:' for Loot++.\n"
                       + "  - weight             : (min: 1 / max: 2147483647) - Higher weights drop more frequently.\n"
                       + "  - min_bury           : (min: 1 / max: 250) - The lowest absolute block depth below the solid surface.\n"
                       + "  - max_bury           : (min: 1 / max: 250) - Random vertical depth modifier variance added onto min_bury.\n"
                       + "  - trap_chance        : (min: 0 / max: 100) - Percent-based chance the chest spawns as a trapped container with TNT.\n"
                       + "  - fuzzy_variance     : (min: 0 / max: 100) - Maximum block distance radius the reported chat coordinates will shift.";

        masterLootPool = config.getStringList("MasterLootPool", "loot engine", defaultPool, comment);

        if (config.hasChanged()) {
            config.save();
        }

        treasureMap = new ItemTreasureMap("1");
        GameRegistry.registerItem(treasureMap, "treasuremap");
        treasureMap2 = new ItemTreasureMap("2");
        GameRegistry.registerItem(treasureMap2, "treasuremap2");
        treasureMap3 = new ItemTreasureMap("3");
        GameRegistry.registerItem(treasureMap3, "treasuremap3");
        treasureMap4 = new ItemTreasureMap("4");
        GameRegistry.registerItem(treasureMap4, "treasuremap4");
        treasureMap5 = new ItemTreasureMap("5");
        GameRegistry.registerItem(treasureMap5, "treasuremap5");
        treasureMap6 = new ItemTreasureMap("6");
        GameRegistry.registerItem(treasureMap6, "treasuremap6");
        treasureMap7 = new ItemTreasureMap("7");
        GameRegistry.registerItem(treasureMap7, "treasuremap7");
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(this);
    }

    @Override
    public String getCommandName() {
        return "burymaploot";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/burymaploot <player> <mapTier>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) return;
        EntityPlayer player = CommandBase.getPlayer(sender, args[0]);
        if (player != null) {
            executeMapRoutine(player, args[1].trim());
        }
    }

    public boolean executeMapRoutine(EntityPlayer player, String mapTier) {
        World world = player.worldObj;
        if (world.isRemote) return false;

        int actualX = (int) player.posX + (random.nextInt(101) - 50);
        int actualZ = (int) player.posZ + (random.nextInt(101) - 50);
        int surfaceY = world.getTopSolidOrLiquidBlock(actualX, actualZ);

        while (surfaceY > 0) {
            Block b = world.getBlock(actualX, surfaceY, actualZ);
            if (b.getMaterial().isSolid() || b.getMaterial() == Material.water || b.getMaterial() == Material.lava) {
                break;
            }
            surfaceY--;
        }

        Block surfaceBlock = world.getBlock(actualX, surfaceY, actualZ);
        if (surfaceBlock.getMaterial() == Material.water) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "\u2620 Find land first!"));
            return false;
        }

        String selectedLootKey = "dungeonChest";
        int minBury = 2, maxBury = 4, trapChance = 5, fuzzyVariance = 6;
        int totalWeight = 0;

        for (String s : masterLootPool) {
            if (s == null || !s.contains("|")) continue;
            String[] split = s.split("\\|");
            if (split.length >= 2) {
                try {
                    totalWeight += Integer.parseInt(split[1].trim());
                } catch (Exception e) {}
            }
        }

        if (totalWeight > 0) {
            int roll = random.nextInt(totalWeight);
            int currentWeight = 0;
            for (String s : masterLootPool) {
                if (s == null || !s.contains("|")) continue;
                String[] split = s.split("\\|");
                if (split.length >= 2) {
                    try {
                        currentWeight += Integer.parseInt(split[1].trim());
                        if (roll < currentWeight) {
                            selectedLootKey = split[0].trim();
                            if (split.length >= 3) minBury = Integer.parseInt(split[2].trim());
                            if (split.length >= 4) maxBury = Integer.parseInt(split[3].trim());
                            if (split.length >= 5) trapChance = Integer.parseInt(split[4].trim());
                            if (split.length >= 6) fuzzyVariance = Integer.parseInt(split[5].trim());
                            break;
                        }
                    } catch (Exception e) {}
                }
            }
        }

        int buryDepth = minBury + random.nextInt(maxBury);
        int targetY = surfaceY - buryDepth;

        Block targetBlockSpace = world.getBlock(actualX, targetY, actualZ);
        boolean isExploitDetected = targetY <= 8 
                || targetBlockSpace == Blocks.bedrock 
                || targetBlockSpace == Blocks.air 
                || targetBlockSpace.getMaterial() == Material.lava;

        if (!isExploitDetected && targetY + 1 <= surfaceY - 1) {
            for (int checkY = targetY + 1; checkY <= surfaceY - 1; checkY++) {
                if (world.getBlock(actualX, checkY, actualZ) == Blocks.air) {
                    isExploitDetected = true;
                    break;
                }
            }
        }

        if (isExploitDetected) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Terrain unstable. Find solid ground!"));
            return false;
        }

        boolean isTrapped = random.nextInt(100) < trapChance;
        Block chestBlock = isTrapped ? Blocks.trapped_chest : Blocks.chest;
        world.setBlock(actualX, targetY, actualZ, chestBlock);

        if (isTrapped) {
            world.setBlock(actualX, targetY - 1, actualZ, Blocks.tnt);
        }

        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(actualX, targetY, actualZ);
        if (tileChest != null) {
            String finalCommand = "";
            if (selectedLootKey.startsWith("rc:")) {
                finalCommand = String.format("setblockloot %d %d %d %s", actualX, targetY, actualZ, selectedLootKey.substring(3));
            } else if (selectedLootKey.startsWith("lpp:")) {
                finalCommand = String.format("lootpp fillchest %d %d %d %s", actualX, targetY, actualZ, selectedLootKey.substring(4));
            } else {
                finalCommand = String.format("lootpp fillchest %d %d %d %s", actualX, targetY, actualZ, selectedLootKey);
            }
            MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), finalCommand);
        }

        world.playSoundEffect(player.posX, player.posY, player.posZ, "ambient.weather.thunder", 0.6F, 1.2F);

        int fuzzyX = actualX + (random.nextInt(fuzzyVariance * 2 + 1) - fuzzyVariance);
        int fuzzyZ = actualZ + (random.nextInt(fuzzyVariance * 2 + 1) - fuzzyVariance);

        String coordMessage = String.format(
            EnumChatFormatting.GOLD + "\u2620 A treasure path has been marked! " +
            EnumChatFormatting.YELLOW + "Search the area around: " + EnumChatFormatting.GREEN + "X: ~%d, Z: ~%d " +
            EnumChatFormatting.AQUA + "(Dig down %d blocks!) " + EnumChatFormatting.GRAY + "[Debug Target: X:%d, Y:%d, Z:%d]", 
            fuzzyX, fuzzyZ, buryDepth, actualX, targetY, actualZ
        );
        player.addChatComponentMessage(new ChatComponentText(coordMessage));
        return true;
    }

    public static class ItemTreasureMap extends Item {
        public ItemTreasureMap(String tier) {
            super();
            this.setMaxStackSize(64);
            String registryName = tier.equals("1") ? "treasuremap" : "treasuremap" + tier;
            this.setUnlocalizedName(registryName);
            this.setTextureName("treasurehunter:" + registryName);
        }

        @Override
        public ItemStack onItemRightClick(ItemStack heldItem, World world, EntityPlayer player) {
            String name = heldItem.getItem().getUnlocalizedName();
            String extractedTier = name.substring(name.length() - 1);
            if (!Character.isDigit(extractedTier.charAt(0))) {
                extractedTier = "1";
            }

            boolean success = false;
            if (!world.isRemote) {
                java.util.List<cpw.mods.fml.common.ModContainer> list = cpw.mods.fml.common.Loader.instance().getModList();
                TreasureHunterMod targetModInstance = null;
                for (cpw.mods.fml.common.ModContainer mod : list) {
                    if (mod.getModId().equals("treasurehunter")) {
                        targetModInstance = (TreasureHunterMod) mod.getMod();
                        break;
                    }
                }
                if (targetModInstance != null) {
                    success = targetModInstance.executeMapRoutine(player, extractedTier);
                }
            } else {
                success = true;
            }

            if (success && !player.capabilities.isCreativeMode) {
                heldItem.stackSize--;
            }
            return heldItem;
        }
    }
}
