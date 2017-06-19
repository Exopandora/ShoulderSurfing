package com.teamderpy.shouldersurfing.json;

public class JsonShoulderSurfing
{
	private JsonVersions[] versions;

	public static class JsonVersions
	{
		private String version;
		private JsonMappings mappings;
		
		public static class JsonMappings
		{
			private JsonMapping[] classes;
			private JsonMapping[] methods;
			private JsonMapping[] fields;
			
			public static class JsonMapping
			{
				private String name;
				private String obf;
				private String desc;
				
				private String pkg;
				private String path;
				
				public String getClassPackage(boolean isObfuscated)
				{
					return isObfuscated ? this.obf : this.pkg;
				}
				
				public String getClassPath(boolean isObfuscated)
				{
					return isObfuscated ? this.obf : this.path;
				}
				
				public String getFieldOrMethod(boolean isObfuscated)
				{
					return isObfuscated ? this.obf : this.name;
				}
				
				public void setPackage(String pkg)
				{
					this.pkg = pkg;
				}
				
				public String getPackage()
				{
					return this.pkg;
				}
				
				public void setPath(String path)
				{
					this.path = path;
				}
				
				public String getPath()
				{
					return this.path;
				}
				
				public void setName(String name)
				{
					this.name = name;
				}
				
				public String getName()
				{
					return this.name;
				}
				
				public void setObf(String obf)
				{
					this.obf = obf;
				}
				
				public String getObf()
				{
					return this.obf;
				}
				
				public void setDescriptor(String desc)
				{
					this.desc = desc;
				}
				
				public String getDescriptor()
				{
					return this.desc;
				}
			}
			
			public void setClasses(JsonMapping[] classes)
			{
				this.classes = classes;
			}

			public JsonMapping[] getClasses()
			{
				return this.classes;
			}

			public void setMethods(JsonMapping[] methods)
			{
				this.methods = methods;
			}

			public JsonMapping[] getMethods()
			{
				return this.methods;
			}

			public void setFields(JsonMapping[] fields)
			{
				this.fields = fields;
			}
			
			public JsonMapping[] getFields()
			{
				return this.fields;
			}
		}

		public void setVersion(String version)
		{
			this.version = version;
		}
		
		public String getVersion()
		{
			return this.version;
		}
		
		public void setMappings(JsonMappings mappings)
		{
			this.mappings = mappings;
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
