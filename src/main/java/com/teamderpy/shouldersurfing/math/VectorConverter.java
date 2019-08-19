package com.teamderpy.shouldersurfing.math;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Vector2f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.0
 * @since 2012-12-24
 */
@OnlyIn(Dist.CLIENT)
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
	public static Vec2f project2D(final Vec3d v3)
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
	public static Vec2f project2D(final float x, final float y, final float z)
	{
		/** Buffer that will hold the screen coordinates */
		FloatBuffer screen_coords = GLAllocation.createDirectFloatBuffer(3);
		
		/** Buffer that holds the transformation matrix of the view port */
		IntBuffer viewport = GLAllocation.createDirectByteBuffer(64).asIntBuffer();
		
		/** Buffer that holds the transformation matrix of the model view */
		FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
		
		/** Buffer that holds the transformation matrix of the projection */
		FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
		
		screen_coords.clear();
		modelview.clear();
		projection.clear();
		viewport.clear();
		
		GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview);
		GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
		
		if(uProject(x, y, z, modelview, projection, viewport, screen_coords))
		{
			float screenX = screen_coords.get(0);
			float screenY = screen_coords.get(1);
			
			if(!Float.isInfinite(screenX) && !Float.isInfinite(screenY))
			{
				return new Vec2f(screenX, screenY);
			}
		}
		
		return null;
	}
	private static void multMatrixVecf(FloatBuffer m, float[] in, float[] out)
	{
		for(int i = 0; i < 4; i++)
		{
			out[i] = in[0] * m.get(m.position() + i) + in[1] * m.get(m.position() + 4 + i) + in[2] * m.get(m.position() + 8 + i) + in[3] * m.get(m.position() + 12 + i);
		}
	}
	
	private static boolean uProject(float objx, float objy, float objz, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer win_pos)
	{
		float[] in = new float[4];
		float[] out = new float[4];
		
		in[0] = objx;
		in[1] = objy;
		in[2] = objz;
		in[3] = 1.0F;
		
		multMatrixVecf(modelMatrix, in, out);
		multMatrixVecf(projMatrix, out, in);
		
		if(in[3] == 0.0F)
		{
			return false;
		}
		
		in[3] = (1.0F / in[3]) * 0.5F;
		
		// Map x, y and z to range 0-1
		in[0] = in[0] * in[3] + 0.5F;
		in[1] = in[1] * in[3] + 0.5F;
		in[2] = in[2] * in[3] + 0.5F;
		
		// Map x,y to viewport
		win_pos.put(0, in[0] * viewport.get(viewport.position() + 2) + viewport.get(viewport.position()));
		win_pos.put(1, in[1] * viewport.get(viewport.position() + 3) + viewport.get(viewport.position() + 1));
		win_pos.put(2, in[2]);
		
		return true;
	}
}
