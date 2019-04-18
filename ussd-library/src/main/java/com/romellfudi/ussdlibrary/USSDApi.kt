package com.romellfudi.ussdlibrary

import java.util.HashMap
import java.util.HashSet

/**
 *
 * @author Romell Dominguez
 * @version 1.1.c 13/02/2018
 * @since 1.0.a
 */
interface USSDApi {
    fun send(text: String, callbackMessage: USSDController.CallbackMessage)
    fun callUSSDInvoke(ussdPhoneNumber: String, map: HashMap<String, HashSet<String>>,
                       callbackInvoke: USSDController.CallbackInvoke)

    fun callUSSDInvoke(ussdPhoneNumber: String, simSlot: Int, map: HashMap<String, HashSet<String>>,
                       callbackInvoke: USSDController.CallbackInvoke)

    fun callUSSDOverlayInvoke(ussdPhoneNumber: String, map: HashMap<String, HashSet<String>>,
                              callbackInvoke: USSDController.CallbackInvoke)

    fun callUSSDOverlayInvoke(ussdPhoneNumber: String, simSlot: Int, map: HashMap<String, HashSet<String>>,
                              callbackInvoke: USSDController.CallbackInvoke)
}
