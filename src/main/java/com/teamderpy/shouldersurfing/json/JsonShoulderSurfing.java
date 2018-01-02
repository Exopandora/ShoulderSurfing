package com.teamderpy.shouldersurfing.json;

import java.util.Map;

public class JsonShoulderSurfing
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
			
			public static abstract class JsonMapping
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
			
			public static class JsonClassMapping extends JsonMapping
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
			
			public static class JsonFieldMapping extends JsonMapping
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
