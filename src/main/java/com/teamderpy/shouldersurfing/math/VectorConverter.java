package com.teamderpy.shouldersurfing.math;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.0
 * @since 2012-12-24
 */
@SideOnly(Side.CLIENT)
public class VectorConverter
{
	/**
	 * Converts a Minecraft world coordinate to a screen coordinate
	 *
	 * The world coordinate is the absolute location of a 3d vector in the
	 * Minecraft world relative to the world origin.
	 * <p>
	 * Note that the return value will be scaled to match the current GUI
	 * resolution of Minecraft.
	 *
	 * @param v3
	 *            {@link Vec3} representing a coordinate in the Minecraft world
	 * @return Returns a {@link Vector2f} representing a 2D location on the
	 *         screen, or null if the vector fails to be converted.
	 */
	public static Vector2f project2D(final Vec3d v3)
	{
		return project2D((float) v3.x, (float) v3.y, (float) v3.z);
	}
	
	/**
	 * Converts a Minecraft world coordinate to a screen coordinate
	 *
	 * The world coordinate is the absolute location of a 3d vector in the
	 * Minecraft world relative to the world origin.
	 * <p>
	 * Note that the return value will be scaled to match the current GUI
	 * resolution of Minecraft.
	 *
	 * @param x
	 *            X coordinate in the Minecraft world
	 * @param y
	 *            Y coordinate in the Minecraft world
	 * @param z
	 *            Z coordinate in the Minecraft world
	 * @return Returns a {@link Vector2f} representing a 2D location on the
	 *         screen, or null if the vector fails to be converted.
	 */
	public static Vector2f project2D(final float x, final float y, final float z)
	{
		/**
		 * Buffer that will hold the screen coordinates
		 */
		FloatBuffer screen_coords = GLAllocation.createDirectFloatBuffer(3);
		
		/**
		 * Buffer that holds the transformation matrix of the view port
		 */
		IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
		
		/**
		 * Buffer that holds the transformation matrix of the model view
		 */
		FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
		
		/**
		 * Buffer that holds the transformation matrix of the projection
		 */
		FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
		
		screen_coords.clear();
		modelview.clear();
		projection.clear();
		viewport.clear();
		
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
		
		/**
		 * the return value of the gluProject call
		 */
		boolean ret = GLU.gluProject(x, y, z, modelview, projection, viewport, screen_coords);
		
		if(ret)
		{
			return new Vector2f(screen_coords.get(0), screen_coords.get(1));
		}
		
		return null;
	}
}
