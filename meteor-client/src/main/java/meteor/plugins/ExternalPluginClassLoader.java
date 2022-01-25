package meteor.plugins;

import lombok.Getter;
import lombok.Setter;
import meteor.eventbus.ReflectUtil;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.net.URLClassLoader;

public class ExternalPluginClassLoader extends URLClassLoader implements ReflectUtil.PrivateLookupableClassLoader
{
	@Getter
	@Setter
	private MethodHandles.Lookup lookup;

	public ExternalPluginClassLoader(URL[] urls)
	{
		super(urls, ExternalPluginClassLoader.class.getClassLoader());
		ReflectUtil.installLookupHelper(this);
	}

	@Override
	public Class<?> defineClass0(String name, byte[] b, int off, int len) throws ClassFormatError
	{
		return super.defineClass(name, b, off, len);
	}
}
