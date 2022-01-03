package meteor.eventbus.events

import net.runelite.api.packets.PacketBufferNode
import net.runelite.api.packets.PacketOpcode
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.util.*

class PacketSent(private var packetBufferNode: PacketBufferNode) {
    fun hexDump(): String {
        val opcode = if (packetBufferNode.clientPacket != null) packetBufferNode.clientPacket.id else -1
        var opcodeText = opcode.toString()
        val payload =
            Arrays.copyOfRange(packetBufferNode.packetBuffer.payload, 1, packetBufferNode.packetBuffer.offset)
        for (op in PacketOpcode.values()) {
            if (opcode == op.opcode) {
                opcodeText = op.toString()
                break
            }
        }
        return "[$opcodeText]\n" +
                hexDump(
                    payload,
                    0,
                    packetBufferNode.packetBuffer.offset
                )
    }

    companion object {
        fun hexDump(array: ByteArray, offset: Int, length: Int): String {
            val width = 16
            val builder = StringBuilder()
            var rowOffset = offset
            while (rowOffset < offset + length) {
                builder.append(String.format("%06d     ", rowOffset))
                for (index in 0 until width) {
                    if (rowOffset + index < array.size) {
                        builder.append(String.format("%02X ", array[rowOffset + index]))
                    } else {
                        builder.append("   ")
                    }
                }
                if (rowOffset < array.size) {
                    val asciiWidth = Math.min(width, array.size - rowOffset)
                    builder.append("     ")
                    builder.append(
                        String(
                            array,
                            rowOffset,
                            asciiWidth,
                            StandardCharsets.UTF_8
                        ).replace("\r\n".toRegex(), " ").replace("\n".toRegex(), " ")
                    )
                }
                builder.append(String.format("%n"))
                rowOffset += width
            }
            return builder.toString()
        }
    }
}