package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Locale;

public class DebugScreenOverlayHandler
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
				String axis = switch(direction)
				{
					case NORTH -> "Towards negative Z";
					case SOUTH -> "Towards positive Z";
					case WEST -> "Towards negative X";
					case EAST -> "Towards positive X";
					default -> "Invalid";
				};
				float yRot = Mth.wrapDegrees(camera.getYRot());
				float xRot = Mth.wrapDegrees(camera.getXRot());
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
