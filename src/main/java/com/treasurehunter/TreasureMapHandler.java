package com.treasurehunter;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

public class TreasureMapHandler {

    private final Random random = new Random();

    @SubscribeEvent
    public void onMapUse(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        World world = player.worldObj;

        if (world.isRemote || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            return;
        }

        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null || heldItem.getItem() == null) {
            return;
        }

        String itemName = Block.blockRegistry.getNameForObject(heldItem.getItem());
        if (itemName == null) {
            return;
        }

        String lootKey = "";
        int trapChance = 0;
        boolean isMatchedMap = false;

        if (TreasureHunterMod.mapConfigurations != null) {
            for (String configLine : TreasureHunterMod.mapConfigurations) {
                if (configLine == null || configLine.trim().isEmpty() || !configLine.contains("|")) {
                    continue;
                }
                
                String[] parts = configLine.trim().split("\\|");
                if (parts.length >= 3) {
                    String configuredItem = parts[0].trim();
                    if (configuredItem.equalsIgnoreCase(itemName.trim())) {
                        lootKey = parts[1].trim();
                        try {
                            trapChance = Integer.parseInt(parts[2].trim());
                        } catch (NumberFormatException e) {
                            trapChance = 0;
                        }
                        isMatchedMap = true;
                        break;
                    }
                }
            }
        }

        if (!isMatchedMap) {
            return;
        }

        int randomX = (int) player.posX + (random.nextInt(101) - 50);
        int randomZ = (int) player.posZ + (random.nextInt(101) - 50);
        
        int surfaceY = world.getTopSolidOrLiquidBlock(randomX, randomZ);

        Block surfaceBlock = world.getBlock(randomX, surfaceY, randomZ);
        Material material = surfaceBlock.getMaterial();

        if (material == Material.water) {
            player.addChatComponentMessage(new ChatComponentText(
                EnumChatFormatting.BOLD.toString() + EnumChatFormatting.RED + "☠ You can't find buried treasure while out at sea! Find land first!"));
            return; 
        }

        if (material == Material.lava || surfaceBlock == Blocks.bedrock || surfaceY <= 5) {
            player.addChatComponentMessage(new ChatComponentText(
                EnumChatFormatting.RED + "The terrain shifts too dangerously here. Find stable ground!"));
            return; 
        }

        int buryDepth = 2 + random.nextInt(4); 
        int targetY = surfaceY - buryDepth;
        if (targetY < 5) targetY = 5;

        boolean isTrapped = random.nextInt(100) < trapChance;
        Block chestBlock = isTrapped ? Blocks.trapped_chest : Blocks.chest;

        world.setBlock(randomX, targetY, randomZ, chestBlock);

        if (isTrapped) {
            world.setBlock(randomX, targetY - 1, randomZ, Blocks.tnt);
            player.addChatComponentMessage(new ChatComponentText(
                EnumChatFormatting.BOLD.toString() + EnumChatFormatting.DARK_RED + "☠ The air grows heavy... this loot looks dangerously guarded!"));
        }

        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(randomX, targetY, randomZ);
        if (tileChest != null && !lootKey.isEmpty()) {
            if (lootKey.startsWith("rc:")) {
                String rcId = lootKey.substring(3);
                String command = String.format("setblockloot %d %d %d %s", randomX, targetY, randomZ, rcId);
                MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
            } else if (lootKey.startsWith("lpp:")) {
                String lppId = lootKey.substring(4);
                String command = String.format("lootpp fillchest %d %d %d %s", randomX, targetY, randomZ, lppId);
                MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
            } else if (lootKey.startsWith("vanilla:")) {
                String vanillaId = lootKey.substring(8);
                ChestGenHooks info = ChestGenHooks.getInfo(vanillaId);
                if (info != null) {
                    WeightedRandomChestContent.generateChestContents(random, info.getItems(random), tileChest, info.getCount(random));
                }
            }
        }

        world.playSoundEffect(player.posX, player.posY, player.posZ, "ambient.weather.thunder", 0.6F, 1.2F);

        String coordMessage = String.format(
            EnumChatFormatting.GOLD + "🏴‍☠ Ahoy! The compass path is locked: " + 
            EnumChatFormatting.YELLOW + "Dig down at: " + 
            EnumChatFormatting.BOLD.toString() + EnumChatFormatting.GREEN + "X: %d, Y: %d, Z: %d " + 
            EnumChatFormatting.RESET.toString() + EnumChatFormatting.AQUA + "(Buried %d blocks deep!)", 
            randomX, targetY, randomZ, buryDepth
        );
        player.addChatComponentMessage(new ChatComponentText(coordMessage));

        if (!player.capabilities.isCreativeMode) {
            heldItem.stackSize--;
            if (heldItem.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
        
        event.setCanceled(true);
    }
}
