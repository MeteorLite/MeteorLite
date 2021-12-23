package meteor.config

import Main
import meteor.config.legacy.Keybind
import meteor.config.legacy.ModifierlessKeybind
import net.runelite.api.coords.WorldPoint
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.time.Duration
import java.time.Instant
import java.util.*

class ConfigManager {
    fun stringToObject(str: String, type: Class<*>): Any? {
        if (type == Boolean::class.javaPrimitiveType || type == Boolean::class.java) {
            return java.lang.Boolean.parseBoolean(str)
        }
        if (type == Int::class.javaPrimitiveType || type == Int::class.java) {
            return try {
                str.toInt()
            } catch (e: NumberFormatException) {
                1
            }
        }
        if (type == Double::class.javaPrimitiveType || type == Double::class.java) {
            return str.toDouble()
        }
        if (type == Color::class.java) {
            return colorFromString(str)
        }
        if (type == Dimension::class.java) {
            val splitStr = str.split("x").toTypedArray()
            val width = splitStr[0].toInt()
            val height = splitStr[1].toInt()
            return Dimension(width, height)
        }
        if (type == Point::class.java) {
            val splitStr = str.split(":").toTypedArray()
            val width = splitStr[0].toInt()
            val height = splitStr[1].toInt()
            return Point(width, height)
        }
        if (type == Rectangle::class.java) {
            val splitStr = str.split(":").toTypedArray()
            val x = splitStr[0].toInt()
            val y = splitStr[1].toInt()
            val width = splitStr[2].toInt()
            val height = splitStr[3].toInt()
            return Rectangle(x, y, width, height)
        }
        if (type.isEnum) {
            return java.lang.Enum.valueOf(type as Class<out Enum<*>?>, str)
        }
        if (type == Instant::class.java) {
            return Instant.parse(str)
        }
        if (type == Keybind::class.java || type == ModifierlessKeybind::class.java) {
            val splitStr = str.split(":").toTypedArray()
            val code = splitStr[0].toInt()
            val mods = splitStr[1].toInt()
            return if (type == ModifierlessKeybind::class.java) {
                ModifierlessKeybind(code, mods)
            } else Keybind(code, mods)
        }
        if (type == WorldPoint::class.java) {
            val splitStr = str.split(":").toTypedArray()
            val x = splitStr[0].toInt()
            val y = splitStr[1].toInt()
            val plane = splitStr[2].toInt()
            return WorldPoint(x, y, plane)
        }
        if (type == Duration::class.java) {
            return Duration.ofMillis(str.toLong())
        }
        if (type == ByteArray::class.java) {
            return Base64.getUrlDecoder().decode(str)
        }
        return if (type == EnumSet::class.java) {
            try {
                val substring = str.substring(str.indexOf("{") + 1, str.length - 1)
                val splitStr = substring.split(", ").toTypedArray()
                var enumClass: Class<out Enum<*>>
                if (!str.contains("{")) {
                    return null
                }
                enumClass = findEnumClass(str, Main::class.java.classLoader)
                val enumSet = EnumSet.noneOf(enumClass)
                for (s in splitStr) {
                    try {
                        enumSet.add(java.lang.Enum.valueOf(enumClass, s.replace("[", "").replace("]", "")))
                    } catch (ignore: IllegalArgumentException) {
                        return EnumSet.noneOf(enumClass)
                    }
                }
                enumSet
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else str
    }

    fun objectToString(`object`: Any?): String? {
        if (`object` is Color) {
            return `object`.rgb.toString()
        }
        if (`object` is Enum<*>) {
            return `object`.name
        }
        if (`object` is Dimension) {
            return `object`.width.toString() + "x" + `object`.height
        }
        if (`object` is Point) {
            return `object`.x.toString() + ":" + `object`.y
        }
        if (`object` is Rectangle) {
            return `object`.x.toString() + ":" + `object`.y + ":" + `object`.width + ":" + `object`.height
        }
        if (`object` is Instant) {
            return `object`.toString()
        }
        if (`object` is Keybind) {
            return `object`.keyCode.toString() + ":" + `object`.modifiers
        }
        if (`object` is WorldPoint) {
            return `object`.x.toString() + ":" + `object`.y + ":" + `object`.plane
        }
        if (`object` is Duration) {
            return `object`.toMillis().toString()
        }
        if (`object` is ByteArray) {
            return Base64.getUrlEncoder().encodeToString(`object` as ByteArray?)
        }
        return if (`object` is EnumSet<*>) {
            if (`object`.size == 0) {
                getElementType(`object` as EnumSet<*>?)!!.canonicalName + "{}"
            } else `object`.toTypedArray()[0].javaClass.canonicalName + "{" + `object` + "}"
        } else `object`?.toString()
    }

    fun getElementType(enumSet: EnumSet<*>?): Class<*>? {
        var enumSet = enumSet
        if (enumSet!!.isEmpty()) {
            enumSet = EnumSet.complementOf(enumSet)
        }
        return enumSet!!.iterator().next().javaClass.declaringClass
    }

    fun findEnumClass(`class`: String,
                      classLoader: ClassLoader): Class<Enum<*>> {
        var transformedString = StringBuilder()
        try {
            val strings = `class`.substring(0, `class`.indexOf("{")).split("\\.").toTypedArray()
            var i = 0
            while (i != strings.size) {
                if (i == 0) {
                    transformedString.append(strings[i])
                } else if (i == strings.size - 1) {
                    transformedString.append("$").append(strings[i])
                } else {
                    transformedString.append(".").append(strings[i])
                }
                i++
            }
            return classLoader.loadClass(transformedString.toString()) as Class<Enum<*>>
        } catch (e2: java.lang.Exception) {
            // Will likely fail a lot
        }
        try {
            return classLoader.loadClass(`class`.substring(0, `class`.indexOf("{"))) as Class<Enum<*>>
        } catch (e: java.lang.Exception) {
            // Will likely fail a lot
        }
        transformedString = StringBuilder()
        throw RuntimeException("Failed to find Enum for " + `class`.substring(0, `class`.indexOf("{")))
    }

    fun colorFromString(string: String): Color? {
        return try {
            val i = Integer.decode(string)
            Color(i, true)
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun splitKey(key: String): Array<String>? {
        var key = key
        val i = key.indexOf('.')
        if (i == -1) {
            // all keys must have a group and key
            return null
        }
        val group = key.substring(0, i)
        key = key.substring(i + 1)
        return arrayOf(group, key)
    }
}