package com.teamderpy.shouldersurfing.asm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.asm.Mappings.JsonMapping.JsonVersions;
import com.teamderpy.shouldersurfing.asm.Mappings.JsonMapping.JsonVersions.JsonMappings.JsonClassMapping;
import com.teamderpy.shouldersurfing.asm.Mappings.JsonMapping.JsonVersions.JsonMappings.JsonFieldMapping;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Mappings
{
	private final Map<String, JsonClassMapping> CLASS_MAPPINGS = new HashMap<String, JsonClassMapping>();
	private final Map<String, JsonFieldMapping> FIELD_MAPPINGS = new HashMap<String, JsonFieldMapping>();
	private boolean isObfuscated;
	
	public Mappings(String... files)
	{
		this.init(files);
	}
	
	private void init(String... files)
	{
		for(String file : files)
		{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			JsonMapping json = new Gson().fromJson(reader, JsonMapping.class);
			
			if(json != null)
			{
				for(JsonVersions versions : json.getVersions())
				{
					if(versions.getVersion().equals(ForgeVersion.mcVersion))
					{
						for(JsonClassMapping klass : versions.getMappings().getClasses())
						{
							CLASS_MAPPINGS.put(klass.getName(), klass);
						}
						
						for(JsonFieldMapping method : versions.getMappings().getMethods())
						{
							FIELD_MAPPINGS.put(method.getName(), method);
						}
						
						for(JsonFieldMapping field : versions.getMappings().getFields())
						{
							FIELD_MAPPINGS.put(field.getName(), field);
						}
						
						ShoulderSurfing.LOGGER.info("Loaded mappings for Minecraft " + versions.getVersion());
						break;
					}
				}
			}
			
			if(CLASS_MAPPINGS.isEmpty() || FIELD_MAPPINGS.isEmpty())
			{
				ShoulderSurfing.LOGGER.error("No mappings found for Minecraft " + ForgeVersion.mcVersion);
			}
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
	
	public static class JsonMapping
	{
		private JsonVersions[] versions;
		
		public static class JsonVersions
		{
			private String version;
			private JsonMappings mappings;
			
			public static class JsonMappings
			{
				private JsonClassMapping[] classes;
				private JsonFieldMapping[] methods;
				private JsonFieldMapping[] fields;
				
				public static abstract class JsonMappingBase
				{
					protected String name;
					protected String obf;
					
					public String getName()
					{
						return this.name;
					}
					
					public String getObf()
					{
						return this.obf;
					}
				}
				
				public static class JsonClassMapping extends JsonMappingBase
				{
					private String path;
					
					public String getPath()
					{
						return this.path + "/" + this.name;
					}
					
					public String getPackage()
					{
						return this.path.replaceAll("/", ".") + "." + this.name;
					}
					
					public String getClassPackage(boolean isObfuscated)
					{
						return isObfuscated ? this.obf : this.getPackage();
					}
					
					public String getClassPath(boolean isObfuscated)
					{
						return isObfuscated ? this.obf : this.getPath();
					}
				}
				
				public static class JsonFieldMapping extends JsonMappingBase
				{
					private String desc;
					
					public String getDescriptor(Map<String, JsonClassMapping> mappings, boolean isObfuscated)
					{
						String result = this.desc;
						
						if(result != null)
						{
							for(JsonClassMapping mapping : mappings.values())
							{
								result = result.replaceAll("L\\$" + mapping.name + ";", "L" + mapping.getClassPath(isObfuscated) + ";");
							}
						}
						
						return result;
					}
					
					public String getFieldOrMethod(boolean isObfuscated)
					{
						return isObfuscated ? this.obf : this.name.split("#")[1];
					}
				}

				public JsonClassMapping[] getClasses()
				{
					return this.classes;
				}

				public JsonFieldMapping[] getMethods()
				{
					return this.methods;
				}

				public JsonFieldMapping[] getFields()
				{
					return this.fields;
				}
			}
			
			public String getVersion()
			{
				return this.version;
			}
			
			public JsonMappings getMappings()
			{
				return this.mappings;
			}
		}
		
		public void setVersions(JsonVersions[] versions)
		{
			this.versions = versions;
		}
		
		public JsonVersions[] getVersions()
		{
			return this.versions;
		}
	}
}
