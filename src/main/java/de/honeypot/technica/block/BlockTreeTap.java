package de.honeypot.technica.block;

import de.honeypot.technica.Technica;
import de.honeypot.technica.init.ModBlocks;
import de.honeypot.technica.init.ModItems;
import de.honeypot.technica.tileentity.TileEntityTreeTap;
import de.honeypot.technica.util.ModEnum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by Chloroplast on 02.09.2017.
 */
public class BlockTreeTap extends BlockContainer {

    public final static String TREE_TAP = "tree_tap";
    public final static int MAX_LOAD = 4;

    protected static final AxisAlignedBB BOUNDS_NORTH = new AxisAlignedBB(0.4375, 0.5625, 0,     0.5625, 0.8125, 0.375);
    protected static final AxisAlignedBB BOUNDS_EAST  = new AxisAlignedBB(0.625,  0.5625, 0.4375,1,      0.8125, 0.5625);
    protected static final AxisAlignedBB BOUNDS_SOUTH = new AxisAlignedBB(0.4375, 0.5625, 0.625, 0.5625, 0.8125, 1);
    protected static final AxisAlignedBB BOUNDS_WEST  = new AxisAlignedBB(0,      0.5625, 0.4375,0.375,  0.8125, 0.5625);


    public enum ENUM_STATE implements IStringSerializable {
        NONE, BUCKET, RESIN;
        public String getName() {
            return this.name().toLowerCase();
        }
    }

    public final static PropertyEnum<ModEnum.ENUM_DIRECTION> BLOCK_DIR = PropertyEnum.<ModEnum.ENUM_DIRECTION>create("dir", ModEnum.ENUM_DIRECTION.class);
    public final static PropertyEnum<ENUM_STATE> BLOCK_STATE = PropertyEnum.create("stat", ENUM_STATE.class);

    public BlockTreeTap() {
        super(Material.CIRCUITS);

        this.setHardness(2.0F);
        this.setSoundType(SoundType.WOOD);
        setTickRandomly(true);


        setRegistryName(TREE_TAP);
        setUnlocalizedName(TREE_TAP);
        ModBlocks.registerBlock(this);
        setCreativeTab(Technica.CREATIVE_TAB_TECHNICA);


        ItemBlock item = new ItemBlock(this);
        item.setRegistryName(TREE_TAP);
        item.setUnlocalizedName(TREE_TAP);
        item.setCreativeTab(Technica.CREATIVE_TAB_TECHNICA);
        ModItems.registerItem(item);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTreeTap();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch(state.getValue(BLOCK_DIR)){
            case NORTH: return BOUNDS_NORTH;
            case EAST:  return BOUNDS_EAST;
            case SOUTH: return BOUNDS_SOUTH;
            case WEST:  return BOUNDS_WEST;
        }
        return null;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(BLOCK_DIR).ordinal() << 2) | state.getValue(BLOCK_STATE).ordinal();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(BLOCK_DIR, ModEnum.ENUM_DIRECTION.values()[meta >> 2])
                .withProperty(BLOCK_STATE, ENUM_STATE.values()[meta & 0x3]);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing facing) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_STATE, BLOCK_DIR);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

        switch(facing) {
            case DOWN:
            case UP:
                return getDefaultState();
            case NORTH:
                return getDefaultState().withProperty(BLOCK_DIR, ModEnum.ENUM_DIRECTION.SOUTH).withProperty(BLOCK_STATE, ENUM_STATE.NONE);
            case EAST:
                return getDefaultState().withProperty(BLOCK_DIR, ModEnum.ENUM_DIRECTION.WEST).withProperty(BLOCK_STATE, ENUM_STATE.NONE);
            case SOUTH:
                return getDefaultState().withProperty(BLOCK_DIR, ModEnum.ENUM_DIRECTION.NORTH).withProperty(BLOCK_STATE, ENUM_STATE.NONE);
            case WEST:
                return getDefaultState().withProperty(BLOCK_DIR, ModEnum.ENUM_DIRECTION.EAST).withProperty(BLOCK_STATE, ENUM_STATE.NONE);
            default:
                return getDefaultState();
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {

        if(!worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)){
            return false;
        }

        IBlockState blockAimed;
        ModEnum.ENUM_DIRECTION aimDir;

        switch(side) {
            case DOWN:
            case UP:
                return false;
            case NORTH:
                aimDir = ModEnum.ENUM_DIRECTION.NORTH;
                blockAimed = worldIn.getBlockState(pos.south());
                break;
            case EAST:
                aimDir = ModEnum.ENUM_DIRECTION.EAST;
                blockAimed = worldIn.getBlockState(pos.west());
                break;
            case SOUTH:
                aimDir = ModEnum.ENUM_DIRECTION.SOUTH;
                blockAimed = worldIn.getBlockState(pos.north());
                break;
            case WEST:
                aimDir = ModEnum.ENUM_DIRECTION.WEST;
                blockAimed = worldIn.getBlockState(pos.east());
                break;
            default:
                return false;
        }

        if(blockAimed.getBlock() != ModBlocks.LOG_RUBBER_LIVING){
            return false;
        }

        if(blockAimed.getValue(BlockLogRubberLiving.LOG_STATUS) != BlockLogRubberLiving.CUT_STATUS.READY){
            return false;
        }


        return (blockAimed.getValue(BlockLogRubberLiving.LOG_DIRECTION) == aimDir);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

        if (stack.hasDisplayName()) {
            ((TileEntityTreeTap) worldIn.getTileEntity(pos)).setCustomName(stack.getDisplayName());
        }

        BlockPos posAimed = null;
        switch(state.getValue(BLOCK_DIR)){
            case NORTH: posAimed = pos.north(); break;
            case EAST:  posAimed = pos.east();  break;
            case SOUTH: posAimed = pos.south(); break;
            case WEST:  posAimed = pos.west();  break;
        }

        worldIn.setBlockState(posAimed,
                ModBlocks.LOG_RUBBER_LIVING.getDefaultState()
                        .withProperty(BlockLogRubberLiving.LOG_DIRECTION, ModEnum.ENUM_DIRECTION.EAST)
                        .withProperty(BlockLogRubberLiving.LOG_STATUS, BlockLogRubberLiving.CUT_STATUS.NONE), 10);



    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {

        BlockPos aim = null;

        switch(state.getValue(BLOCK_DIR)){
            case NORTH: aim = pos.north(); break;
            case EAST:  aim = pos.east();  break;
            case SOUTH: aim = pos.south(); break;
            case WEST:  aim = pos.west();  break;
        }

        if(worldIn.getBlockState(aim).getBlock() != ModBlocks.LOG_RUBBER_LIVING){
            return;
        }

        TileEntityTreeTap te = (TileEntityTreeTap) worldIn.getTileEntity(pos);
        InventoryHelper.dropInventoryItems(worldIn, pos, te);

        worldIn.setBlockState(aim,
                ModBlocks.LOG_RUBBER_LIVING.getDefaultState()
                        .withProperty(BlockLogRubberLiving.LOG_DIRECTION, state.getValue(BLOCK_DIR).getMirrored())
                        .withProperty(BlockLogRubberLiving.LOG_STATUS, BlockLogRubberLiving.CUT_STATUS.DRY), 10);

    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntityTreeTap te = (TileEntityTreeTap) worldIn.getTileEntity(pos);
        if(te.getContent() == null || te.getContent().isEmpty()){
            return 0;
        }
        if(te.getContent().getItem() == Items.BUCKET){
            return 1;
        }

        return 2;
    }

    /**
     * Called from rubberLog if this Block is connected to it
     */
    public void dropFromMissingSource(World worldIn, IBlockState state, BlockPos pos){

        TileEntityTreeTap te = (TileEntityTreeTap) worldIn.getTileEntity(pos);
        InventoryHelper.dropInventoryItems(worldIn, pos, te);

        worldIn.spawnEntity(new EntityItem(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(Item.getItemFromBlock(ModBlocks.TREE_TAP))));
        worldIn.setBlockToAir(pos);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {

        if(state.getValue(BLOCK_STATE) == ENUM_STATE.BUCKET){
            TileEntityTreeTap te = (TileEntityTreeTap) worldIn.getTileEntity(pos);
            ++te.load;

            if(te.load >= MAX_LOAD){
                te.load = 0;
                te.setInventorySlotContents(0, new ItemStack(ModItems.BUCKET_RESIN, 1));
                worldIn.setBlockState(pos, state.withProperty(BLOCK_STATE, ENUM_STATE.RESIN), 10);
            }
        }
    }
}
