package com.badkraft.foundations.world.level.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;


public class ClayBlock extends Block {
    public ClayBlock() {
        super(BlockBehaviour.Properties
                .of(Material.CLAY, MaterialColor.COLOR_GRAY)
                .strength(0.5f)
                .sound(SoundType.GRAVEL));
    }
}
