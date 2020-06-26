package com.teamderpy.shouldersurfing.math;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VectorConverter
{
	public static Vec2f project2D(Vector3d position, MatrixStack matrixStackIn, Matrix4f projectionIn)
	{
		IntBuffer viewport = GLAllocation.createDirectByteBuffer(64).asIntBuffer();
		FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
		FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
		
		modelview.clear();
		projection.clear();
		viewport.clear();
		
		matrixStackIn.getLast().getMatrix().write(modelview);
		projectionIn.write(projection);
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
		
		Vec2f screen = VectorConverter.uProject(position, modelview, projection, viewport);
		
		if(screen == null || Float.isInfinite(screen.getX()) || Float.isInfinite(screen.getY()))
		{
			return null;
		}
		
		return screen;
	}
	
	private static void multMatrixVecf(FloatBuffer m, float[] in, float[] out)
	{
		for(int i = 0; i < 4; i++)
		{
			out[i] = in[0] * m.get(m.position() + i) + in[1] * m.get(m.position() + 4 + i) + in[2] * m.get(m.position() + 8 + i) + in[3] * m.get(m.position() + 12 + i);
		}
	}
	
	private static Vec2f uProject(Vector3d position, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport)
	{
		float[] in = new float[4];
		float[] out = new float[4];
		
		in[0] = (float) position.getX();
		in[1] = (float) position.getY();
		in[2] = (float) position.getZ();
		in[3] = 1.0F;
		
		VectorConverter.multMatrixVecf(modelMatrix, in, out);
		VectorConverter.multMatrixVecf(projMatrix, out, in);
		
		if(in[3] == 0.0F)
		{
			return null;
		}
		
		in[3] = (1.0F / in[3]) * 0.5F;
		
		in[0] = in[0] * in[3] + 0.5F;
		in[1] = in[1] * in[3] + 0.5F;
		in[2] = in[2] * in[3] + 0.5F;
		
		float x = in[0] * viewport.get(viewport.position() + 2) + viewport.get(viewport.position());
		float y = in[1] * viewport.get(viewport.position() + 3) + viewport.get(viewport.position() + 1);
		
		return new Vec2f(x, y);
	}
}
