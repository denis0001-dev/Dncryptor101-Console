package ru.morozovit.dncryptor101

import javafx.application.Application
import ru.morozovit.dncryptor101.base.Solver
import ru.morozovit.dncryptor101.base.Solver.Companion.ALPHABET_RUSSIAN
import ru.morozovit.dncryptor101.gui.App
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    val version = "1.2"
    val solver = Solver()
    println(
        """
            Dncryptor101 
            Version $version
            ------------------ 
        """.trimIndent()
    )
    while (true) {
        print(
            """
                Select an option:
                0. Exit
                1. Decrypt text
                2. Encrypt text
                3. About
                4. Enter GUI
                Type a number: 
            """.trimIndent()
        )
        val choice: Int
        try {
            choice = readln().toInt()
        } catch (_: NumberFormatException) {
            println("Try again.")
            continue
        }
        when (choice) {
            0 -> break
            1 -> {
                print("Enter a word to decrypt: ")
                val inp = readln()
                print("Shift (0/Enter if you don't know (list all variants)): ")
                var shift: Int
                while (true) {
                    try {
                        val inp1 = readln()
                        shift = if (inp1 == "") 0 else inp1.toInt()
                        break
                    } catch (e: NumberFormatException) {
                        println("Your input is not a number. Try again.")
                        continue
                    }
                }
                if (shift != 0) {
                    val word = solver.decrypt(inp, shift)
                    println("Your decrypted word: $word")
                    break
                }
                println("\nPossible variants:")
                val words = solver.decrypt(inp)
                var col = 1
                for (word in words) {
                    print(word)
                    col++
                    if (col == 3 + 1 || word.length >= 20) {
                        col = 1
                        println()
                    } else {
                        print(" ")
                    }
                }
                println("Best variant: ")
                val wordsString = words.let {
                    var str = ""
                    for (word in words) {
                        str += word + "\n"
                    }
                    str
                }

                val folderId = "b1g169s6edl2jluerhop"
                val token = "t1.9euelZrOjJ2NlZmZi5zMl5zKmp6Zku3rnpWajZ2KzJGNnpCTlpHNnZDLi83l9PcHEiNF-e8tVymw3fT3R0AgRfnvLVcpsM3n9euelZqdjMbIl5zHncmQjZyWm4uQju_8xeuelZqdjMbIl5zHncmQjZyWm4uQjg.nsn7ixeftSFFR-Tao_Y5FempOWKHNX8460E84abnvdMTMrcLYbX6j7IFNEhVRBvC2F4hxPfgOYImm_TBNLuICg"
                val url = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion"

                // Создаем URL и соединение
                val urlObj = URI(url).toURL()
                val connection = urlObj.openConnection() as HttpURLConnection

                // Устанавливаем заголовки
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.setRequestProperty("x-folder-id", folderId)

                connection.doOutput = true
                val os = connection.outputStream
                val osw = OutputStreamWriter(os, "UTF-8")
                osw.write(
                    """
                    {
                        "modelUri": "gpt://$folderId/yandexgpt-lite",
                        "completionOptions": {
                            "stream": false,
                            "temperature": 0.1,
                            "maxTokens": "1000"
                        },
                        "messages": [
                            {
                                "role": "system",
                                "text": "Какое из этих предложений правильное? Ответь только правильным предложением."
                            },
                            {
                                "role": "user",
                                "text": "$wordsString"
                            }
                        ]
                    }
                    """.trimIndent()
                )
                osw.flush()
                osw.close()
                os.close()

                val responseCode = connection.responseCode
                println("Response code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseInputStream = connection.inputStream
                    val responseString = responseInputStream.bufferedReader().use { it.readText() }
                    println(responseString)
                } else {
                    println("Error: Server responded with code $responseCode")
                }
                break
            }
            2 -> {
                print("Type a word to encrypt: ")
                val inp = readln()
                print("Shift (0 to generate a random one): ")
                var shift: Int

                while (true) {
                    try {
                        shift = readln().toInt()
                        break
                    } catch (_: NumberFormatException) {
                        println("Your input is not a number. Try again.")
                        continue
                    }
                }
                if (shift == 0) {
                    shift = Random.nextInt(
                        -ALPHABET_RUSSIAN.length..ALPHABET_RUSSIAN.length
                    )
                    println("Your shift: $shift")
                }
                val word = solver.encrypt(inp, shift)
                println("Your encrypted word: $word")

                break
            }
            3 -> {
                println(
                    """
                        
                        A simple program to encrypt and decrypt text.
                        Version $version
                    """.trimIndent()
                )
                break
            }
            4 -> {
                Application.launch(App::class.java)
                break
            }
        }
    }
}