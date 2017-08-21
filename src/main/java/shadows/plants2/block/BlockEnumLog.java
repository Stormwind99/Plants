package shadows.plants2.block;

import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shadows.plants2.block.base.BlockEnum;
import shadows.plants2.client.RenamedStateMapper;
import shadows.plants2.data.enums.ILogBasedPropertyEnum;
import shadows.plants2.util.PlantUtil;

public class BlockEnumLog<E extends Enum<E> & ILogBasedPropertyEnum> extends BlockEnum<E> {

	static final PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class);

	public BlockEnumLog(String name, Class<E> enumClass, int predicate) {
		super(name, Material.WOOD, SoundType.WOOD, 2.0F, 1.0F, enumClass, "type", (e) -> (e.getPredicateIndex() == predicate));
		this.setDefaultState(getBlockState().getBaseState().withProperty(property, types.get(0)).withProperty(AXIS, Axis.Y));
	}

	@Override
	public BlockStateContainer createStateContainer() {
		return new BlockStateContainer(this, property, AXIS);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (worldIn.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
			for (BlockPos blockpos : BlockPos.getAllInBox(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
				IBlockState iblockstate = worldIn.getBlockState(blockpos);

				if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos)) {
					iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos);
				}
			}
		}
	}

	@Override
	public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isWood(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(property, types.get(meta % 5)).withProperty(AXIS, Axis.values()[meta / 5]);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(property, types.get(meta)).withProperty(AXIS, facing.getAxis());
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(property).ordinal() % 4;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = state.getValue(property).ordinal() % 5; // 0-4
		i += (state.getValue(AXIS).ordinal() * 5); // 0-14
		return i;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModels(ModelRegistryEvent e) {
		for (int i = 0; i < types.size(); i++) {
			PlantUtil.sMRL("logs", this, i, AXIS.getName() + "=" + EnumAxis.Y.getName() + "," + property.getName() + "=" + types.get(i).getName());
		}
		ModelLoader.setCustomStateMapper(this, new RenamedStateMapper("logs"));
	}

}