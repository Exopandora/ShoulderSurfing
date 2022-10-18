package com.teamderpy.shouldersurfing.asm;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraftforge.fml.relauncher.FMLInjectionData;

public class Mappings
{
	private final Map<String, ClassMapping> mappings;
	
	public Mappings(Map<String, ClassMapping> mapping)
	{
		this.mappings = mapping;
	}
	
	@Nullable
	public String map(String name, boolean obf)
	{
		ClassMapping mapping = this.mappings.get(name);
		
		if(mapping != null)
		{
			return mapping.get(obf);
		}
		
		return null;
	}
	
	@Nullable
	public String desc(String name, boolean obf)
	{
		ClassMapping mapping = this.mappings.get(name);
		
		if(mapping != null && mapping instanceof DescMapping)
		{
			return ((DescMapping) mapping).desc(this.mappings, obf);
		}
		
		return null;
	}
	
	public boolean isObfuscated(String klassName)
	{
		for(ClassMapping klass : this.mappings.values())
		{
			if(klass.getName().equals(klassName))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static Mappings load(String file)
	{
		InputStream is = Mappings.class.getClassLoader().getResourceAsStream(file);
		JsonObject json = new JsonParser().parse(new InputStreamReader(is)).getAsJsonObject();
		Map<String, ClassMapping> mappings = new HashMap<String, ClassMapping>();
		
		try
		{
			String version = (String) FMLInjectionData.data()[4];
			
			for(Entry<String, JsonElement> entry : json.getAsJsonObject("classes").entrySet())
			{
				JsonObject object = entry.getValue().getAsJsonObject();
				ClassMapping klass = new ClassMapping(object.get("name").getAsString(), Mappings.readObf(object, version));
				mappings.put(entry.getKey(), klass);
			}
			
			Mappings.loadDescMappings(json.getAsJsonObject("methods").entrySet(), mappings, version);
			Mappings.loadDescMappings(json.getAsJsonObject("fields").entrySet(), mappings, version);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Unable to load mappings");
		}
		
		return new Mappings(mappings);
	}
	
	private static void loadDescMappings(Set<Entry<String, JsonElement>> set, Map<String, ClassMapping> mappings, String version)
	{
		for(Entry<String, JsonElement> entry : set)
		{
			JsonObject object = entry.getValue().getAsJsonObject();
			DescMapping klass = new DescMapping(object.get("name").getAsString(), object.get("desc").getAsString(), Mappings.readObf(object, version));
			mappings.put(entry.getKey(), klass);
		}
	}
	
	private static String readObf(JsonObject object, String version)
	{
		JsonElement obf = object.get("obf");
		
		if(obf.isJsonPrimitive())
		{
			return obf.getAsString();
		}
		
		return obf.getAsJsonObject().get(version).getAsString();
	}
	
	private static class ClassMapping
	{
		private final String name;
		private final String obf;
		
		public ClassMapping(String name, String obf)
		{
			this.name = name;
			this.obf = obf;
		}
		
		public String get(boolean obf)
		{
			return obf ? this.getObf() : this.getName();
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getObf()
		{
			return this.obf;
		}
	}
	
	private static class DescMapping extends ClassMapping
	{
		private final String desc;
		
		public DescMapping(String name, String desc, String obf)
		{
			super(name, obf);
			this.desc = desc;
		}
		
		public String desc(Map<String, ClassMapping> mappings, boolean obf)
		{
			String desc = this.desc;
			
			for(Entry<String, ClassMapping> mapping : mappings.entrySet())
			{
				desc = desc.replace("L$" + mapping.getKey() + ";", "L" + mapping.getValue().get(obf) + ";");
			}
			
			return desc;
		}
	}
}
