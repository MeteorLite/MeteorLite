package com.owain.chinmanager.cookies.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.inject.Singleton;
import okhttp3.Cookie;
import meteor.Logger;

@Singleton
public class SerializableCookie implements Serializable
{
	private static final String TAG = SerializableCookie.class.getSimpleName();

	private static final long serialVersionUID = -8594045714036645534L;

	private transient Cookie cookie;

	private Logger log = Logger.Companion.getLogger(SerializableCookie.class);

	public String encode(Cookie cookie)
	{
		this.cookie = cookie;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;

		try
		{
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(this);
		}
		catch (IOException e)
		{
			log.debug(TAG, "IOException in encodeCookie", e);
			return null;
		}
		finally
		{
			if (objectOutputStream != null)
			{
				try
				{
					objectOutputStream.close();
				}
				catch (IOException e)
				{
					log.debug(TAG, "Stream not closed in encodeCookie", e);
				}
			}
		}

		return byteArrayToHexString(byteArrayOutputStream.toByteArray());
	}

	private static String byteArrayToHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte element : bytes)
		{
			int v = element & 0xff;
			if (v < 16)
			{
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}

	public Cookie decode(String encodedCookie)
	{
		byte[] bytes = hexStringToByteArray(encodedCookie);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			bytes);

		Cookie cookie = null;
		ObjectInputStream objectInputStream = null;
		try
		{
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			cookie = ((SerializableCookie) objectInputStream.readObject()).cookie;
		}
		catch (IOException e)
		{
			log.debug(TAG, "IOException in decodeCookie", e);
		}
		catch (ClassNotFoundException e)
		{
			log.debug(TAG, "ClassNotFoundException in decodeCookie", e);
		}
		finally
		{
			if (objectInputStream != null)
			{
				try
				{
					objectInputStream.close();
				}
				catch (IOException e)
				{
					log.debug(TAG, "Stream not closed in decodeCookie", e);
				}
			}
		}

		return cookie;
	}

	private static byte[] hexStringToByteArray(String hexString)
	{
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
		{
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
				.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}

	private static final long NON_VALID_EXPIRES_AT = -1L;

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(cookie.name());
		out.writeObject(cookie.value());
		out.writeLong(cookie.persistent() ? cookie.expiresAt() : NON_VALID_EXPIRES_AT);
		out.writeObject(cookie.domain());
		out.writeObject(cookie.path());
		out.writeBoolean(cookie.secure());
		out.writeBoolean(cookie.httpOnly());
		out.writeBoolean(cookie.hostOnly());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		Cookie.Builder builder = new Cookie.Builder();

		builder.name((String) in.readObject());

		builder.value((String) in.readObject());

		long expiresAt = in.readLong();
		if (expiresAt != NON_VALID_EXPIRES_AT)
		{
			builder.expiresAt(expiresAt);
		}

		final String domain = (String) in.readObject();
		builder.domain(domain);

		builder.path((String) in.readObject());

		if (in.readBoolean())
		{
			builder.secure();
		}

		if (in.readBoolean())
		{
			builder.httpOnly();
		}

		if (in.readBoolean())
		{
			builder.hostOnlyDomain(domain);
		}

		cookie = builder.build();
	}
}
