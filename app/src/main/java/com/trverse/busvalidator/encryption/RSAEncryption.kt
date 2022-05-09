package com.trverse.busvalidator.encryption

import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPrivateKeySpec
import javax.crypto.Cipher
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class RSAEncryption(context_: Context) {
    var getPublicKey: String? = null
    private var context: Context? = null
    private val RSA = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"
    var publicKey: PublicKey? = null
    var privateKey: PrivateKey? = null
    val parentNode = "RSAKeyValue"

    init {
        context = context_
    }

    fun loadPrivateKey() {
        var reader: BufferedReader? = null
        val assestReader: InputStream? = context?.assets?.open("rsa-private-key.pem")
        reader = BufferedReader(
            InputStreamReader(assestReader, "UTF-8")
        )
        val lines: MutableList<String?> = ArrayList()
        var line: String? = null
        while (reader.readLine().also { line = it } != null) lines.add(line)
        if (lines.size > 1 && lines[0]!!.startsWith("-----") && lines[lines.size - 1]!!.startsWith("-----")) {
            lines.removeAt(0)
            lines.removeAt(lines.size - 1)
        }
        val sb = StringBuilder()
        for (aLine in lines) sb.append(aLine)
        val keyString = sb.toString()
        val keyBytes = Base64.decode(keyString, Base64.DEFAULT)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        privateKey = keyFactory.generatePrivate(spec)
    }


    fun encrypt(text: String?): String? {
        try {
            return Base64.encodeToString(
                encrypt(
                    text!!,
                    publicKey!!
                ), Base64.DEFAULT
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(java.lang.Exception::class)
    private fun encrypt(text: String, pubRSA: PublicKey): ByteArray? {
        val cipher: Cipher = Cipher.getInstance(RSA)
        cipher.init(Cipher.ENCRYPT_MODE, pubRSA)
        return cipher.doFinal(text.toByteArray())
    }

    fun decrypt(data: String?): String? {
        try {
            val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                java.util.Base64.getDecoder().decode(data)
            } else {
                Base64.decode(data, Base64.DEFAULT)
            }
            val data = decrypt(decoded)
            return String(data!!).filter { !it.isISOControl() }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(java.lang.Exception::class)
    private fun decrypt(src: ByteArray): ByteArray? {
        val cipher = Cipher.getInstance(RSA)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(src)
    }

    private fun convertStringToDocument(xmlStr: String): Document? {
        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val builder: DocumentBuilder
        try {
            builder = factory.newDocumentBuilder()
            return builder.parse(InputSource(StringReader(xmlStr)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun loadPrivateKeyFromXMl(xmlStr: String) {
        var mod = ""
        var d = ""
        var expo = ""
        val xmlParser = XMLParser()
        val document = xmlParser.getDomElement(
            "<RSAKeyValue><Modulus>rWnvwZkC6cfi+VD7g3azs9cVWyAOGM9FLTD014m8eMDQcO6GdmPGelguQjtIOaKDTwEGa3ipOSstef4X449WqilFg8H3qFqdPaGYECRLZqivtm7ScnLDMMkwkd7+kkLeGxgzpJ+j5thOzgj4S520774OxMvW1H5YVidTA66ZTMkmf8KDngbvZTJWU/in42xSXUVRX8OJH6ebbVXBFHuoOARvF7ALElVoUz6XsC7jDwS8ncWJODWkON952yyoEuOv7of80b+pFOF5pR0Aoq/j0jvNxYiQ/vSYmxeVFtt+Ck57En6hCCmimY9SFkRhaiWMv8loJeYOg84vBPDGPIWfdyrYYaRDFEpopo8RdUCxx13HYOB7E1kyi6bMhLaVxTzqmnBNGe7PMqVt7vjR/G0IL1altULN/MRy0AaEOZcDm/7ae3ZVRv+Lux/vDA7zHkCA4pQQUG4zDMuEKNKlgLAM6OGzUI4SqlvWXm4EBm+siGrE0kQAwO7tBewcimD/gwax4rHv3AtR8MksDz8Rf1PGxn4wnDGxs5O6fE1eyWPXSkepDe2z6zFTlnjEtzZkPJFQkUudXp6o+29oIO2j5LWaotYQfezvhK4KAC/Labq2+e5v+U6rGdrT8ZAaCtZ1uKME5N/5IKv2p9oVjW66yWC06Gn23lbX96fntqBlwwMLAws=</Modulus><Exponent>AQAB</Exponent><P>3IXyHZ5LG+ebiCE4MfRHds/3tebIKb5L+z7RAZ8epkTQjx3/0KVgQDDpGtBMS3hZv0a16TLoz6tDGGQggNB1ix2QiKqDDw+ng4FZn/xWqLuBXA2QDyKaCYxd96orfxD5WYRSYL8ULFHN0jkj1OEiHnMo57jgkOc5q7Tq2E7i4HeE8vD2tSOu2gC9PNSjimYPdiCEVIry3CV/p5ynts0F0aKMOXvWWoozapB5K96IwpcIgUPoJkbyo18MEELNgXVDVGjtW8ydXMgT4FMIRFAA2BBCr9B0zKY+11zNdqCzKrMOOQFr/6uGzCecv58qWcIr/KD1TwAc73GjdDS/3DD7Bw==</P><Q>yU/U0J0DIjr/w/9fwIhAnpYN5eccCkLVrkfYIbXj4RRWqR0sY+pVDIkFQ8iyCbPrR/56sRld1U/vN7EPPF4y49os8kvHJu1cE/TDw5cmpRkhXCUrQq3+olnTOfDwnYnmbv/OeB1HZIithnv2i3gcmgjLSdHjmhmqSsrcw6egM6uZsv3N3DMTYXrhKMLR2TtVv7OBUbMPQBMnuh07QY1wxaKB7LXDFhIM2bdi0wWpi3z0DYuHDnZTT3F7xqsxENYh4WsLsHytc9gOUKaVZf69FjsAeJfd8wVCr5B3pf/jrhSo1J+SOmugxphu/9VunVwbHTI5478rZEaDkD4TMIHC3Q==</Q><DP>2gek5hvHbwODBwixItK+hX4BD9vpMzxuqy3kP5IvH30SJy4l6whXLaAuGNCxXjzTnwYufS5UeF5/q/HzIOj6Dl1gtX7ArXdwy9hr4H4AwDsDHxxZvl0bpy0dCu04rB0AK71kJTtGmrsWoC7WendM4IZDfzeO8DVtE5OATfVQ41XIINqVUWrCSdBJSYeDWiamSsjDjUMD8x0wJWwBdf8nLalkssD5ofLZ+xZ0dydCWAIz6npVYOLsqxIiP0GNJd3mf+JuK0EWpNE8taHinofrDbSl+mZD1MLZhty+V/dyrVjzSvFY8iOI1vijReH8YVpVWyVY5I7cDPFyBkbRGsdiqQ==</DP><DQ>aiDR+uCrpEIiUDuzSRcLqg9FaGx8VdSCPgzWHn97kU9dmG4u1sXU7HTX97I4WfEmGqBIIlFTLH+lh2CqvZKTlZeRlnLonNcJcePZyKVI1ET5V1CpC9Aaq5BbUPJ6hzMJ7n6hZKVa299KS9nSUhiSS4pTUal7KeRZ6HAmDmeyh0ryDnDLNEkg8IUNaqW6Z6FmRVfm1yFk6syuZum+MYLVbvuLLzVrkZkWeTi6urEDfuvy5aknHDK5HOqg7E2X4l5hIiuC0BoKf0Xp1g7cOBchrDSSvQCjG9ksHM42pabPaxitINq3BmFzMv9tlSX52K+UA8JUzoIb6BNcjJCNIUdnaQ==</DQ><InverseQ>joLG9s7VVJ4k9cxxz44M15qr6UQuZm9PQNMDHDPQc8bf4oQxkc0YOUUi85a9DftK8lrsA/TKvtPoa+m0b5ch3UUHLqUhUmd/lMufz4Jk2SJm316XFV6bcceu+XZLMJj8JCy4E0Y9LQexEQ4b+8Xojh7pYACo++XSTeLWBg5rFpEE+xROU4Djl2DhHBi4tdWdbD3+rjkCSmGjgoXdY2+N3Xaby4LkQVXWX0vWfzGyTOiHDt9LP6sIbKzd5CjqMgiAJJZcADr+aprHIgQrgmIhUPxhpij8xG7T1y3VUham39T9Qfdww6/n3fRd1pmt2C8EJV9AI8u85xOjBDWPf/v4/A==</InverseQ><D>b3OOa9ZzUNzuu12YyD0Z8ZNiXlaTuM49n3W/ZmdXgiQp0ur58Ezf2vGhiV+gY6pWygfWoGvEFsHxUF5F7DLLeEFeM/gNu13BgULCAF3TN/9iLtW622N2n3wugxHWexoOtUswpbUUGeEbhL+vFKofB5YW19RKFfe9vmE3sT4duIPvAl15Sa8bXOalJQLCpB+SL+/FBM8e6gFGm1Zp6UgwLRBsdcTm2LmbeyTXSJxZweVBdATo0/9ltEesgXiJ7Nn40E0F56hOeuUO0Vd6eRSiTxOwhCvpRHLfQY4+e5pFv0oBPzmzNRPNNPQvxa/dC+NmOjyveuwImB2DiqFURsF6YoVBzzz+cgKhYct8Ej+ZaTqTl5zj0VEOGRh2NVDGZl1bRbnbi/YPp/fCeECmMGgVJtotJhVGiE5hCjOd2a7udjsbdw5/VegUmVBUqM/rff15RvF/z72WaRuI4wXVMB6zQ7Wz5e2JIcrDaIbhWkXfOZYjztX+jp08vfak8WjBB/5UUxuD+BqlnJ3jsCOVk52L6KIhH4AwsvYjszxflteuOj87hcAruW8qZXUGab2ZxP7BiLZvhFeG7fVNWU6HSYmX+/tn6d6X9sieqdaD76r70tQkxAyDDoQd0SOUat+sYzMfbrupeTnGcyNliRuk+Khj7vkadg2077ojdyC7Wg3xXUE=</D></RSAKeyValue>"
        )
        val nodeList = document.getElementsByTagName(parentNode)
        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as Element
            mod = xmlParser.getValue(element, "Modulus").trim()
            d = xmlParser.getValue(element, "D").trim()
            expo = xmlParser.getValue(element, "Exponent").trim()
            Log.d("loadingXML", "loadPrivateKeyFromXMl: $mod ")
            Log.d("loadingXML", "loadPrivateKeyFromXMl: $d")
            Log.d("loadingXML", "loadPrivateKeyFromXMl: $expo")
        }

        val decodedMod = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getDecoder().decode(mod)
        } else {
            Base64.decode(mod, Base64.DEFAULT)
        }

        val decodedD = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getDecoder().decode(d)
        } else {
            Base64.decode(d, Base64.DEFAULT)
        }
        val modBigInteger = BigInteger(1, decodedMod)
        val dBigInteger = BigInteger(1, decodedD)
        val keyFactory = KeyFactory.getInstance("RSA")
        val cipher = Cipher.getInstance(RSA)
        val privateKeySpec = RSAPrivateKeySpec(modBigInteger, dBigInteger)
        privateKey = keyFactory.generatePrivate(privateKeySpec)



 /*    val  dc= decrypt("hvYpcTIRsThDVudwNgLLZE7cyszhDLQ27z8cLd1+VJMxHhEpNsF9DCbt++95v5/lUvWPhlshQK/v+IgBiriDiNStq6uxKODP412i77BZb29hD5lasDf73ae0OLlgEnAvSKaYN3l3gIcBSXrd4bCWn/bE2ZznRXzWiq2EDvDdEjBftsLIqudNQyGMvq+eFxITT1JaBOY29L5G21ePLXRHUOH1zmRQXgHtFJmyqdN+qxglFx6fB1oll8JniAChDKr0MsUqVYRfVpgnXIPxK7jXZWbcztTXKqdGte3HGashQ34+YTy9rttanGqtmoecrh5dVkQEozc+UTKkvZklwLHidb3inP3CqFCmKH8nxogqV+5WzkDXydIDjh5TQh9hYiLq9EWQUvZ2R9wivlMTb9L1xpkwLQbPgtTlSAYsBpQXDsT98T6Gg+WPpMkEdhgWwjS76cHU+nlH7D7OLFJ4Jmtf2lZh3aNabRWJBF8kbc2Z0qP+RePeKn8PP5C4Gp42irIcTpCU/UBcTGZ5j3W5lqLHQxxTHmiptnX1TgPgysranMNfQU7eOBfEiUSrobAuvYHDEv8l4dM4tMuaXUdkVatkc8UaGN9Pf7ryiyjt/ZOYm66Eigld9qd1tNDdvQh22u5W7syml6tYAzlG6LJwyRUtRYJ9qfV9BdNkDATQ7B5j2vo=")
        Log.d("result", "loadPrivateKeyFromXMl: $dc")*/

    /*   val decodedEncString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getDecoder().decode("hvYpcTIRsThDVudwNgLLZE7cyszhDLQ27z8cLd1+VJMxHhEpNsF9DCbt++95v5/lUvWPhlshQK/v+IgBiriDiNStq6uxKODP412i77BZb29hD5lasDf73ae0OLlgEnAvSKaYN3l3gIcBSXrd4bCWn/bE2ZznRXzWiq2EDvDdEjBftsLIqudNQyGMvq+eFxITT1JaBOY29L5G21ePLXRHUOH1zmRQXgHtFJmyqdN+qxglFx6fB1oll8JniAChDKr0MsUqVYRfVpgnXIPxK7jXZWbcztTXKqdGte3HGashQ34+YTy9rttanGqtmoecrh5dVkQEozc+UTKkvZklwLHidb3inP3CqFCmKH8nxogqV+5WzkDXydIDjh5TQh9hYiLq9EWQUvZ2R9wivlMTb9L1xpkwLQbPgtTlSAYsBpQXDsT98T6Gg+WPpMkEdhgWwjS76cHU+nlH7D7OLFJ4Jmtf2lZh3aNabRWJBF8kbc2Z0qP+RePeKn8PP5C4Gp42irIcTpCU/UBcTGZ5j3W5lqLHQxxTHmiptnX1TgPgysranMNfQU7eOBfEiUSrobAuvYHDEv8l4dM4tMuaXUdkVatkc8UaGN9Pf7ryiyjt/ZOYm66Eigld9qd1tNDdvQh22u5W7syml6tYAzlG6LJwyRUtRYJ9qfV9BdNkDATQ7B5j2vo=")
        } else {
            Base64.decode("hvYpcTIRsThDVudwNgLLZE7cyszhDLQ27z8cLd1+VJMxHhEpNsF9DCbt++95v5/lUvWPhlshQK/v+IgBiriDiNStq6uxKODP412i77BZb29hD5lasDf73ae0OLlgEnAvSKaYN3l3gIcBSXrd4bCWn/bE2ZznRXzWiq2EDvDdEjBftsLIqudNQyGMvq+eFxITT1JaBOY29L5G21ePLXRHUOH1zmRQXgHtFJmyqdN+qxglFx6fB1oll8JniAChDKr0MsUqVYRfVpgnXIPxK7jXZWbcztTXKqdGte3HGashQ34+YTy9rttanGqtmoecrh5dVkQEozc+UTKkvZklwLHidb3inP3CqFCmKH8nxogqV+5WzkDXydIDjh5TQh9hYiLq9EWQUvZ2R9wivlMTb9L1xpkwLQbPgtTlSAYsBpQXDsT98T6Gg+WPpMkEdhgWwjS76cHU+nlH7D7OLFJ4Jmtf2lZh3aNabRWJBF8kbc2Z0qP+RePeKn8PP5C4Gp42irIcTpCU/UBcTGZ5j3W5lqLHQxxTHmiptnX1TgPgysranMNfQU7eOBfEiUSrobAuvYHDEv8l4dM4tMuaXUdkVatkc8UaGN9Pf7ryiyjt/ZOYm66Eigld9qd1tNDdvQh22u5W7syml6tYAzlG6LJwyRUtRYJ9qfV9BdNkDATQ7B5j2vo=", Base64.DEFAULT)
        }
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        Log.d("drcString", "drcString: ${String(cipher.doFinal(decodedEncString)).filter { 
            it.isISOControl()
        }}")*/


     }
}