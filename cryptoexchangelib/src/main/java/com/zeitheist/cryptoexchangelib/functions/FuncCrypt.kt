package com.zeitheist.cryptoexchangelib.functions

import com.kyoapps.zkotlinextensions.extensions.toHex
import okio.ByteString
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.zip.GZIPInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.zip.GZIPOutputStream


internal object FuncCrypt {

    fun shaHashHex(input: String, bit: Int): String {
        val md = MessageDigest.getInstance("SHA-$bit")
        md.update(input.toByteArray(Charsets.UTF_8))
        return md.digest().toHex()
    }

    fun hmacShaHexString(msg: String, key: String, bit: Int): String {
        return hmacShaByteArray(msg, key.toByteArray(Charsets.UTF_8), bit).toHex()
    }
    fun hmacShaHexString(msg: String, key: ByteArray, bit: Int): String {
        return hmacShaByteArray(msg, key, bit).toHex()
    }

    fun hmacShaByteArray(msg: String, key: ByteArray, bit: Int): ByteArray {
        val type = "HmacSHA$bit"
        val secret = SecretKeySpec(key, type)
        return Mac.getInstance(type).run {
            init(secret)
            doFinal(msg.toByteArray(Charsets.UTF_8))
        }
    }


    fun compress(str: String?): ByteArray? {
        if (str.isNullOrEmpty()) return null
        val obj = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(obj)
        gzip.write(str.toByteArray(charset("UTF-8")))
        gzip.flush()
        gzip.close()
        return obj.toByteArray()
    }


    fun decompress(compressed: ByteArray?): String {
        val outStr = StringBuilder()
        if (compressed == null || compressed.isEmpty()) return ""

        val gis = GZIPInputStream(ByteArrayInputStream(compressed))
        val bufferedReader = BufferedReader(InputStreamReader(gis, "UTF-8"))

        while (true) {
            val line = bufferedReader.readLine()?: break
            outStr.append(line)
        }

        return outStr.toString()
    }

    fun decompress(byteString: ByteString) = decompress(byteString.toByteArray())


}