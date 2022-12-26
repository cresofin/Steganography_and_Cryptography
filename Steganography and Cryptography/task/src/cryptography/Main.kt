package cryptography

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when(val word =readln()) {
            "exit" -> return(println("Bye!"))
            "hide" -> {
                println("Input image file:")
                val inputFileName = readln()
                println("Output image file:")
                val imgFileNameOut = readln()
                val image: BufferedImage = ImageIO.read(File(inputFileName))
                println("Message to hide:")
                val message = readln().encodeToByteArray()
                println("Password: ")
                val password = readln().encodeToByteArray()
                val codifiedMessage: MutableList<Int> = mutableListOf()
                val codifiedPassword: MutableList<Int> = mutableListOf()
                for (i in  message) {
                    // convert byte to binary
                    val messageToBinary: String = Integer.toBinaryString(i.toInt()).padStart(8, '0')
                    for (bit in messageToBinary) {
                        codifiedMessage.add(bit.digitToInt())
                    }
                }
                for (i in  password) {
                    // convert byte to binary
                    val passwordToBinary: String = Integer.toBinaryString(i.toInt()).padStart(8, '0')
                    for (bit in passwordToBinary) {
                        codifiedPassword.add(bit.digitToInt())
                    }
                }
                val encryptedMessage = xor(codifiedMessage, codifiedPassword)
                val limitmessage = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1)
                for (i in limitmessage.indices) {
                    encryptedMessage.add(limitmessage[i])
                }
                if (encryptedMessage.size > image.width * image.height) {
                    println("The input image is not large enough to hold this message.")
                }
                var index = 0
                for (y in 0 until image.height) {
                    for (x in 0 until image.width) {
                        if (index > encryptedMessage.size - 1) break
                        val color = Color(image.getRGB(x, y))
                        val b = if (encryptedMessage[index] % 2 == 0) color.blue and 254 else color.blue or 1
                        val newColor = Color(color.red, color.green, b).rgb
                        image.setRGB(x, y, newColor)
                        index++
                    }
                }
                val outputFile = File(imgFileNameOut)
                ImageIO.write(image, "png", outputFile)
                println("Message saved in $imgFileNameOut image.")
            }
            "show" -> {
                println("Input image file:")
                val imgFileNameIn = readln()
                println("Password:")
                val password = readln().encodeToByteArray()
                val myfileIn = File(imgFileNameIn)
                val image: BufferedImage = ImageIO.read(myfileIn)
                var message = ""
                loop@ for (y in 0 until image.height) {
                    for (x in 0 until image.width) {
                        val color = Color(image.getRGB(x, y))
                        val b = color.blue.toString(2)
                        message += b.last()
                        if ("000000000000000000000011" in message){
                            break@loop
                        }
                    }
                }
                val codifiedPassword: MutableList<Int> = mutableListOf()
                for (i in  password) {
                    // convert byte to binary
                    val passwordToBinary: String = Integer.toBinaryString(i.toInt()).padStart(8, '0')
                    for (bit in passwordToBinary) {
                        codifiedPassword.add(bit.digitToInt())
                    }
                }
                message = message.substring(0,(message.lastIndex + 1) - 23)
                val codifiedMessage = mutableListOf<Int>()
                for (i in message.indices) {
                    codifiedMessage.add(message[i].digitToInt())
                }
                val decodifiedMessage = xor(codifiedMessage, codifiedPassword)
                val chars = decodifiedMessage.size / 8
                val phrase = mutableListOf<Char>()
                var index1 = 0
                var index2 = 8
                var words : Char
                repeat(chars){
                    words = decodifiedMessage.subList(index1,index2).joinToString("").toInt(2).toChar()
                    phrase.add(words)
                    index1 = index2
                    index2 += 8
                }
                println("Message: ")

                println(phrase.joinToString(""))
            }
            else -> println("Wrong task: $word")
        }
    }
}

fun xor (message : MutableList<Int>, password : MutableList<Int>) : MutableList<Int> {
    val encryptedMessage = mutableListOf<Int>()
    val encrypt = (message.size  / password.size)
    var index = 0
    repeat(encrypt) {
        for (i in password.indices) {
            encryptedMessage.add(password[i] xor message[index])
            index++
        }
    }
    return if (message.size == password.size) {
        encryptedMessage
    } else {
        val restmessage = message.subList(index, message.lastIndex + 1)
        for (i in restmessage.indices) {
            encryptedMessage.add(password[i] xor restmessage[i])
        }
        encryptedMessage
    }
}

