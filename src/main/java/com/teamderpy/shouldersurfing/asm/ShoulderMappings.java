package com.teamderpy.shouldersurfing.asm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.json.JsonShoulderSurfing;
import com.teamderpy.shouldersurfing.json.JsonShoulderSurfing.JsonVersions;
import com.teamderpy.shouldersurfing.json.JsonShoulderSurfing.JsonVersions.JsonMappings.JsonClassMapping;
import com.teamderpy.shouldersurfing.json.JsonShoulderSurfing.JsonVersions.JsonMappings.JsonFieldMapping;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class ShoulderMappings
{
	private final Map<String, JsonClassMapping> CLASS_MAPPINGS = new HashMap<String, JsonClassMapping>();
	private final Map<String, JsonFieldMapping> FIELD_MAPPINGS = new HashMap<String, JsonFieldMapping>();
	private boolean isObfuscated;
	
	public ShoulderMappings()
	{
		this.init();
	}
	
	public void init()
	{
		try
		{
			String version = MinecraftForge.MC_VERSION;
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("assets/shouldersurfing/mappings/mappings.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			JsonShoulderSurfing json = new Gson().fromJson(reader, JsonShoulderSurfing.class);
			
			if(json != null)
			{
				for(JsonVersions versions : json.getVersions())
				{
					if(versions.getVersion().equals(version))
					{
						// System.out.println("Found version " + versions.getVersion());
						
						for(JsonClassMapping klass : versions.getMappings().getClasses())
						{
							// System.out.println("Found class " + clazz.getName());
							CLASS_MAPPINGS.put(klass.getName(), klass);
						}
						
						for(JsonFieldMapping method : versions.getMappings().getMethods())
						{
							// System.out.println("Found method " + method.getName());
							FIELD_MAPPINGS.put(method.getName(), method);
						}
						
						for(JsonFieldMapping field : versions.getMappings().getFields())
						{
							// System.out.println("Found field " + field.getName());
							FIELD_MAPPINGS.put(field.getName(), field);
						}
						
						ShoulderSurfing.LOGGER.info("Loaded mappings for Minecraft " + versions.getVersion());
						break;
					}
				}
			}
			
			if(CLASS_MAPPINGS.isEmpty() || FIELD_MAPPINGS.isEmpty())
			{
				ShoulderSurfing.LOGGER.error("No mappings found for Minecraft " + version);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setObfuscated(boolean isObfuscated)
	{
		this.isObfuscated = isObfuscated;
	}

	@Nullable
	public String getClassPackage(String klass)
	{
		if(CLASS_MAPPINGS.containsKey(klass))
		{
			return CLASS_MAPPINGS.get(klass).getClassPackage(this.isObfuscated);
		}
		
		return null;
	}
	
	@Nullable
	public String getClassPath(String klass)
	{
		if(CLASS_MAPPINGS.containsKey(klass))
		{
			return CLASS_MAPPINGS.get(klass).getClassPath(this.isObfuscated);
		}
		
		return null;
	}

	@Nullable
	public String getFieldOrMethod(String fieldOrMethod)
	{
		if(FIELD_MAPPINGS.containsKey(fieldOrMethod))
		{
			return FIELD_MAPPINGS.get(fieldOrMethod).getFieldOrMethod(this.isObfuscated);
		}
		
		return null;
	}

	@Nullable
	public String getPackage(String klass)
	{
		if(CLASS_MAPPINGS.containsKey(klass))
		{
			return CLASS_MAPPINGS.get(klass).getPackage();
		}
		
		return null;
	}

	@Nullable
	public String getPath(String klass)
	{
		if(CLASS_MAPPINGS.containsKey(klass))
		{
			return CLASS_MAPPINGS.get(klass).getPath();
		}
		
		return null;
	}

	@Nullable
	public String getName(String field)
	{
		if(CLASS_MAPPINGS.containsKey(field))
		{
			return CLASS_MAPPINGS.get(field).getName();
		}
		
		if(FIELD_MAPPINGS.containsKey(field))
		{
			return FIELD_MAPPINGS.get(field).getName();
		}
		
		return null;
	}

	@Nullable
	public String getObf(String field)
	{
		if(CLASS_MAPPINGS.containsKey(field))
		{
			return CLASS_MAPPINGS.get(field).getObf();
		}
		
		if(FIELD_MAPPINGS.containsKey(field))
		{
			return FIELD_MAPPINGS.get(field).getObf();
		}
		
		return null;
	}

	@Nullable
	public String getDescriptor(String field)
	{
		if(FIELD_MAPPINGS.containsKey(field))
		{
			return FIELD_MAPPINGS.get(field).getDescriptor(this.CLASS_MAPPINGS, this.isObfuscated);
		}
		
		return null;
	}
}
