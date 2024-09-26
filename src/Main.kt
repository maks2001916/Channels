import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

suspend fun main() {
    val storage = Storage()
    var stringResult = ""
    val time = measureTimeMillis {
        coroutineScope {
            val channelOne = getList(storage.text)
            val channelTwo = modifiedList(channelOne)
            channelTwo.consumeEach {
                stringResult += it
            }
            val listOfChars = dividingIntoLines(stringResult)
            for (i in listOfChars) {
                println(i[0])
            }
        }
    }
    println("Время затрачено на операцию: $time")
}

suspend fun CoroutineScope.getList(text: String): ReceiveChannel<String> = produce {
    val strings = dividingIntoLines(text)
    for (i in strings) {
        delay(10L)
        send(i)
    }

}

fun dividingIntoLines(text: String): List<String> {
    var result = mutableListOf<String>()
    var firstChar = 0
    var lastChar = 0
    var firstCharIndex = 0
    for (i in 0..text.length - 1) {
        if (text[i].isUpperCase() && i != firstCharIndex) {
            lastChar = i
            result.add(text.substring(firstChar, lastChar))
            firstChar = i
        }
    }
    return result
}

suspend fun CoroutineScope.modifiedList(channel: ReceiveChannel<String>): ReceiveChannel<String> = produce {
    channel.consumeEach {
        if (it[0].isLowerCase()) {
            send(it.replaceFirstChar { it.uppercase() })
        } else {
            send(it)
        }
    }
}