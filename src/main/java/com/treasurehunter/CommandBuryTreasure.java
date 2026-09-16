package com.treasurehunter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraft.util.WeightedRandomChestContent;

import java.util.Random;

public class CommandBuryTreasure extends CommandBase {

    private final Random random = new Random();

    @Override
    public String getCommandName() {
        return "burymaploot";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/burymaploot <player> <lootKey> <trapChance>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Allows players or automated systems to run it
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 3) return;

        EntityPlayer player = CommandBase.getPlayer(sender, args[0]);
        if (player == null) return;

        World world = player.worldObj;
        if (world.isRemote) return;

        String lootKey = args[1];
        int trapChance = Integer.parseInt(args[2]);

        int randomX = (int) player.posX + (random.nextInt(101) - 50);
        int randomZ = (int) player.posZ + (random.nextInt(101) - 50);
        int surfaceY = world.getTopSolidOrLiquidBlock(randomX, randomZ);

        Block surfaceBlock = world.getBlock(randomX, surfaceY, randomZ);
        Material material = surfaceBlock.getMaterial();

        if (material == Material.water) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "☠ You can't find buried treasure while out at sea! Find land first!"));
            return;
        }

        if (material == Material.lava || surfaceBlock == Blocks.bedrock || surfaceY <= 5) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "The terrain shifts too dangerously here. Find stable ground!"));
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
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "☠ The air grows heavy... this loot looks dangerously guarded!"));
        }

        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(randomX, targetY, randomZ);
        if (tileChest != null) {
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
            EnumChatFormatting.GOLD + "刻 Ahoy! The compass path is locked: " + 
            EnumChatFormatting.YELLOW + "Dig down at: " + 
            EnumChatFormatting.GREEN + "X: %d, Y: %d, Z: %d " + 
            EnumChatFormatting.AQUA + "(Buried %d blocks deep!)", 
            randomX, targetY, randomZ, buryDepth
        );
        player.addChatComponentMessage(new ChatComponentText(coordMessage));
    }
}
