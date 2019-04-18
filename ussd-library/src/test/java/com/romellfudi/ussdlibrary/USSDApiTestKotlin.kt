package com.romellfudi.ussdlibrary

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.view.accessibility.AccessibilityEvent

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.Matchers.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito.doAnswer
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.api.mockito.PowerMockito.`when`

/**
 * @version 1.0
 * @autor Romell Domínguez
 * @date 10/4/18
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(USSDController::class, USSDService::class, Uri::class)
class USSDApiTestKotlin {

    @Mock
    internal var activity: Activity? = null

    @InjectMocks
    internal var ussdController = USSDController.getInstance(activity!!)

    @InjectMocks
    internal var ussdService = USSDService()

    @Mock
    internal var applicationInfo: ApplicationInfo? = null

    @Mock
    internal var callbackInvoke: USSDController.CallbackInvoke? = null

    @Mock
    internal var callbackMessage: USSDController.CallbackMessage? = null

    @Mock
    internal var uri: Uri? = null

    @Mock
    internal var string: String? = null

    @Mock
    internal var accessibilityEvent: AccessibilityEvent? = null

    @Captor
    internal var stringArgumentCaptor: ArgumentCaptor<String>? = null

    @Mock
    internal var ussdInterface: USSDInterface? = null

    @Test
    fun verifyAccesibilityAccessTest() {
        `when`(activity!!.applicationInfo).thenReturn(applicationInfo)
        applicationInfo!!.nonLocalizedLabel = javaClass.getPackage()!!.toString()
        USSDController.verifyAccesibilityAccess(activity!!)
    }

    @Test
    fun callUSSDInvokeTest() {
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockStatic(USSDController::class.java)
        mockStatic(Uri::class.java)
        `when`(USSDController.verifyAccesibilityAccess(any(Activity::class.java))).thenReturn(true)
        `when`(Uri.decode(any(String::class.java))).thenReturn(string)
        `when`(Uri.parse(any(String::class.java))).thenReturn(uri)
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`<Activity>(activity).startActivity(any(Intent::class.java))
        `when`(accessibilityEvent!!.className).thenReturn("amigo.app.AmigoAlertDialog")
        val texts = object : ArrayList<CharSequence>() {

        }

        var MESSAGE = "waiting"
        texts.add(MESSAGE)
        `when`(accessibilityEvent!!.text).thenReturn(texts)
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify<USSDController.CallbackInvoke>(callbackInvoke).over(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))

        MESSAGE = "problem UUID"
        texts.removeAt(0)
        texts.add(MESSAGE)
        `when`(accessibilityEvent!!.text).thenReturn(texts)
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify<USSDController.CallbackInvoke>(callbackInvoke, times(2)).over(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
    }

    @Test
    fun callUSSDLoginWithNotInputText() {
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockStatic(USSDController::class.java)
        mockStatic(USSDService::class.java)
        mockStatic(Uri::class.java)
        `when`(USSDController.verifyAccesibilityAccess(any(Activity::class.java))).thenReturn(true)
        `when`(Uri.decode(any(String::class.java))).thenReturn(string)
        `when`(Uri.parse(any(String::class.java))).thenReturn(uri)
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`<Activity>(activity).startActivity(any(Intent::class.java))
        `when`(accessibilityEvent!!.className).thenReturn("amigo.app.AmigoAlertDialog")
        val texts = object : ArrayList<CharSequence>() {

        }

        val MESSAGE = "loading"
        texts.add(MESSAGE)
        `when`(accessibilityEvent!!.text).thenReturn(texts)
        `when`(USSDService.notInputText(accessibilityEvent!!)).thenReturn(true)
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify<USSDController.CallbackInvoke>(callbackInvoke, times(1)).over(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))

    }

    @Test
    fun callUSSDSendMultipleMessages() {
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockStatic(USSDController::class.java)
        mockStatic(USSDService::class.java)
        mockStatic(Uri::class.java)
        `when`(USSDController.verifyAccesibilityAccess(any(Activity::class.java))).thenReturn(true)
        `when`(Uri.decode(any(String::class.java))).thenReturn(string)
        `when`(Uri.parse(any(String::class.java))).thenReturn(uri)
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`<Activity>(activity).startActivity(any(Intent::class.java))
        `when`(accessibilityEvent!!.className).thenReturn("amigo.app.AmigoAlertDialog")
        val texts = object : ArrayList<CharSequence>() {

        }

        var MESSAGE = "waiting"
        texts.add(MESSAGE)
        `when`(accessibilityEvent!!.text).thenReturn(texts)
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify<USSDController.CallbackInvoke>(callbackInvoke, times(1)).over(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))

        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`<USSDInterface>(ussdInterface).sendData(any(String::class.java))
        MESSAGE = "Return a message from GATEWAY USSD"
        texts.removeAt(0)
        texts.add(MESSAGE)
        `when`(accessibilityEvent!!.text).thenReturn(texts)
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(1)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(2)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(3)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(4)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))

        MESSAGE = "Final Close dialog"
        texts.removeAt(0)
        texts.add(MESSAGE)
        `when`(USSDService.notInputText(accessibilityEvent!!)).thenReturn(true)
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackInvoke>(callbackInvoke, times(2)).over(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
    }

    @Test
    fun callUSSDOverlayInvokeMultipleMessages() {
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockStatic(USSDController::class.java)
        mockStatic(USSDService::class.java)
        mockStatic(Uri::class.java)
        `when`(USSDController.verifyAccesibilityAccess(any(Activity::class.java))).thenReturn(true)
        `when`(USSDController.verifyOverLay(any(Activity::class.java))).thenReturn(true)
        `when`(Uri.decode(any(String::class.java))).thenReturn(string)
        `when`(Uri.parse(any(String::class.java))).thenReturn(uri)
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`<Activity>(activity).startActivity(any(Intent::class.java))
        `when`(accessibilityEvent!!.className).thenReturn("amigo.app.AmigoAlertDialog")
        val texts = object : ArrayList<CharSequence>() {

        }

        var MESSAGE = "waiting"
        texts.add(MESSAGE)
        `when`(accessibilityEvent!!.text).thenReturn(texts)
        ussdController.callUSSDOverlayInvoke("*1#", map, callbackInvoke!!)
        verify<USSDController.CallbackInvoke>(callbackInvoke, times(1)).over(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))

        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`<USSDInterface>(ussdInterface).sendData(any(String::class.java))
        MESSAGE = "Return a message from GATEWAY USSD"
        texts.removeAt(0)
        texts.add(MESSAGE)
        `when`(accessibilityEvent!!.text).thenReturn(texts)
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(1)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(2)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(3)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackMessage>(callbackMessage, times(4)).responseMessage(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))

        MESSAGE = "Final Close dialog"
        texts.removeAt(0)
        texts.add(MESSAGE)
        `when`(USSDService.notInputText(accessibilityEvent!!)).thenReturn(true)
        ussdController.send("1", callbackMessage!!)
        verify<USSDController.CallbackInvoke>(callbackInvoke, times(2)).over(stringArgumentCaptor!!.capture())
        assertThat(stringArgumentCaptor!!.value, `is`(equalTo(MESSAGE)))
    }
}