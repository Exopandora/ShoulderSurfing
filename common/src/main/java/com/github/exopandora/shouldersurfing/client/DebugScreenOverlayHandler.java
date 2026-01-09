package com.github.exopandora.shouldersurfing.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Locale;

public class DebugScreenOverlayHandler
{
	public static void appendDebugText(List<String> lines)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing() && !Minecraft.getInstance().showOnlyReducedInfo() && instance.isCameraDecoupled())
		{
			int index = findFacingDebugTextIndex(lines);
			
			if(index != -1 && (index + 1 == lines.size() || index + 1 < lines.size() && !lines.get(index + 1).startsWith("Camera: ")))
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
				lines.add(index + 1, String.format(Locale.ROOT, "Camera: %s (%s) (%.1f / %.1f)", direction, axis, yRot, xRot));
			}
		}
	}
	
	private static int findFacingDebugTextIndex(List<String> lines)
	{
		for(int x = 0; x < lines.size(); x++)
		{
			if(lines.get(x).startsWith("Facing: "))
			{
				return x;
			}
		}
		return -1;
	}
}
