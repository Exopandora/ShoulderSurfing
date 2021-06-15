package com.teamderpy.shouldersurfing.asm;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
	public String getDesc(String name, boolean obf)
	{
		ClassMapping mapping = this.mappings.get(name);
		
		if(mapping != null && mapping instanceof DescMapping)
		{
			return ((DescMapping) mapping).desc(this.mappings.values(), obf);
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
		Map<String, ClassMapping> mapping = new HashMap<String, ClassMapping>();
		String version = null;
		
		try
		{
			version = ForgeVersion.class.getDeclaredField("mcVersion").get(ForgeVersion.class).toString();
		}
		catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			e.printStackTrace();
		}
		
		for(Entry<String, JsonElement> entry : json.getAsJsonObject("classes").entrySet())
		{
			JsonObject object = entry.getValue().getAsJsonObject();
			ClassMapping klass = new ClassMapping(object.get("name").getAsString(), object.get("obf").getAsJsonObject().get(version).getAsString());
			mapping.put(entry.getKey(), klass);
		}
		
		for(Set<Entry<String, JsonElement>> sets : Lists.newArrayList(json.getAsJsonObject("methods").entrySet(), json.getAsJsonObject("fields").entrySet()))
		{
			for(Entry<String, JsonElement> entry : sets)
			{
				JsonObject object = entry.getValue().getAsJsonObject();
				DescMapping klass = new DescMapping(object.get("name").getAsString(), object.get("desc").getAsString(), object.get("obf").getAsJsonObject().get(version).getAsString());
				mapping.put(entry.getKey(), klass);
			}
		}
		
		return new Mappings(mapping);
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
		
		public String desc(Collection<ClassMapping> mappings, boolean obf)
		{
			String desc = this.desc;
			
			for(ClassMapping mapping : mappings)
			{
				String[] split = mapping.getName().split("/");
				desc = desc.replace("L$" + split[split.length - 1] + ";", "L" + mapping.get(obf) + ";");
			}
			
			return desc;
		}
	}
}
