package com.romellfudi.ussdlibrary

/**
 * @autor Romell Domínguez
 * @date 2019-04-17
 * @version 1.0
 */
inline fun CharSequence.isEmpty(): Boolean = length == 0
fun String.replace(oldChar: String, newChar: String): String {
    return (this as java.lang.String).replace(oldChar, newChar)
}
fun String.contains(sequence: String): Boolean {
    return (this as java.lang.String).contains(sequence)
}
fun String.substring(indexA: Int): String {
    return (this as java.lang.String).substring(indexA)
}
fun String.indexOf(indexA: String): Int {
    return (this as java.lang.String).indexOf(indexA)
}
fun String.toLowerCase(): String {
    return (this as java.lang.String).toLowerCase()
}