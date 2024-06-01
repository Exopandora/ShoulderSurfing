package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Locale;

public class DebugOverlayGuiHandler
{
	public static void appendDebugText(List<String> left)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing() && !Minecraft.getInstance().showOnlyReducedInfo() && Config.CLIENT.isCameraDecoupled())
		{
			int index = findFacingDebugTextIndex(left);
			
			if(index != -1)
			{
				ShoulderSurfingCamera camera = instance.getCamera();
				Direction direction = Direction.fromYRot(camera.getYRot());
				String axis;
				switch(direction)
				{
					case NORTH:
						axis = "Towards negative Z";
						break;
					case SOUTH:
						axis = "Towards positive Z";
						break;
					case WEST:
						axis = "Towards negative X";
						break;
					case EAST:
						axis = "Towards positive X";
						break;
					default:
						axis = "Invalid";
						break;
				}
				float yRot = MathHelper.wrapDegrees(camera.getYRot());
				float xRot = MathHelper.wrapDegrees(camera.getXRot());
				left.add(index + 1, String.format(Locale.ROOT, "Camera: %s (%s) (%.1f / %.1f)", direction, axis, yRot, xRot));
			}
		}
	}
	
	private static int findFacingDebugTextIndex(List<String> left)
	{
		for(int x = 0; x < left.size(); x++)
		{
			if(left.get(x).startsWith("Facing: "))
			{
				return x;
			}
		}
		return -1;
	}
}
