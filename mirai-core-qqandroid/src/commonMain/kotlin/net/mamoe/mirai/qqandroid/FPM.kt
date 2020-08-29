package net.mamoe.mirai.qqandroid

import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.minutesToMillis
import kotlin.jvm.Synchronized

data class FPM(
    val id: Int,
    val size: Int,
    val time: Long
) {
    val messages = arrayOfNulls<MessageChain>(size)

    @Synchronized
    fun putMessage(index: Int, message: MessageChain): MessageChain? {
        messages[index] = message
        var a = true
        for (m in messages) {
            if (m != null) continue
            a = false
            break
        }
        var m = buildMessageChain {}
        return if (a) {
            for (mm in messages) m += mm!!
            m
        } else null
    }
}

object FPMM{

        private val fpmMap = HashMap<Int, FPM>()
        lateinit var getTime: () -> Long

        @Synchronized
        fun getFpmOrNew(id: Int, size: Int): FPM {
            var fpm = fpmMap[id]
            if (fpm == null) {
                fpm = FPM(id, size, getTime())
                fpmMap[id] = fpm
            }
            return fpm
        }

        @Synchronized
        fun removeFpm(id: Int) {
            fpmMap.remove(id)
        }

        @Synchronized
        fun clear() {
            val time = getTime()
            val i = fpmMap.iterator()
            while (i.hasNext()) {
                val item = i.next()
                if (time - item.value.time > 2.minutesToMillis) i.remove()
            }
        }

}