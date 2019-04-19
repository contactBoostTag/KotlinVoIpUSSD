package com.romellfudi.ussdlibrary

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.view.accessibility.AccessibilityEvent
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import java.util.*

/**
 * @version 1.0
 * @autor Romell Domínguez
 * @date 18/4/19
 */
@PrepareForTest(Uri::class)
class USSDApiTestKotlin {

    @Mock
    internal val activity: Activity = mock()

    @InjectMockKs
    internal var ussdController = USSDController.getInstance(activity)

    @InjectMockKs
    internal var ussdService = USSDService()

    @MockK
    lateinit var applicationInfo: ApplicationInfo

    @MockK
    lateinit var callbackInvoke: USSDController.CallbackInvoke

    @MockK
    lateinit var callbackMessage: USSDController.CallbackMessage

    @MockK
    lateinit var uri: Uri

    @MockK
    lateinit var accessibilityEvent: AccessibilityEvent

    val stringArgumentCaptor = slot<String>()

    @Mock
    internal val ussdInterface: USSDInterface = mock()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun verifyAccesibilityAccessTest() {
        `when`(activity.applicationInfo).thenReturn(applicationInfo)
        `when`(activity.getSystemService(any())).thenReturn(null)
        applicationInfo!!.nonLocalizedLabel = javaClass.getPackage()!!.toString()
        USSDController.verifyAccesibilityAccess(activity)
    }

    @Test
    fun callUSSDInvokeTest() {
        `when`(activity.applicationInfo).thenReturn(applicationInfo)
        `when`(activity.getSystemService(any())).thenReturn(null)
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockkObject(USSDController)
        mockkObject(USSDController.Companion)
        mockkStatic(Uri::class)
        every { USSDController.verifyAccesibilityAccess(any()) } returns true
        every { Uri.decode(any()) } returns ""
        every { Uri.parse(any()) } returns uri
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`(activity).startActivity(any())

        every { accessibilityEvent.className } returns "amigo.app.AmigoAlertDialog"
        val texts = object : ArrayList<CharSequence>() {}
        var MESSAGE = "waiting"
        texts.add(MESSAGE)

        every { accessibilityEvent.text } returns texts
        every { accessibilityEvent.source } returns null
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        MESSAGE = "problem UUID"
        texts.removeAt(0)
        texts.add(MESSAGE)
        every { accessibilityEvent.text } returns texts
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))
    }

    @Test
    fun callUSSDLoginWithNotInputText() {
        `when`(activity.applicationInfo).thenReturn(applicationInfo)
        `when`(activity.getSystemService(any())).thenReturn(null)
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockkObject(USSDController)
        mockkObject(USSDController.Companion)
        mockkObject(USSDService)
        mockkObject(USSDService.Companion)
        mockkStatic(Uri::class)
        every { USSDController.verifyAccesibilityAccess(any()) } returns true
        every { Uri.decode(any()) } returns ""
        every { Uri.parse(any()) } returns uri
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`(activity).startActivity(any())

        every { accessibilityEvent.className } returns "amigo.app.AmigoAlertDialog"
        val texts = object : ArrayList<CharSequence>() {}
        var MESSAGE = "loading"
        texts.add(MESSAGE)
        every { accessibilityEvent.text } returns texts

        every { USSDService.notInputText(any()) } returns true
        every { accessibilityEvent.source } returns null
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))
    }

    @Test
    fun callUSSDSendMultipleMessages() {
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockkObject(USSDController)
        mockkObject(USSDController.Companion)
        mockkObject(USSDService)
        mockkObject(USSDService.Companion)
        mockkStatic(Uri::class)

        every { USSDController.verifyAccesibilityAccess(any()) } returns true
        every { Uri.decode(any()) } returns ""
        every { Uri.parse(any()) } returns uri
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`(activity).startActivity(any())

        every { accessibilityEvent.className } returns "amigo.app.AmigoAlertDialog"
        val texts = object : ArrayList<CharSequence>() {}

        var MESSAGE = "waiting"
        texts.add(MESSAGE)
        every { accessibilityEvent.text } returns texts
        every { accessibilityEvent.source } returns null
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`(ussdInterface).sendData(any())

        MESSAGE = "Return a message from GATEWAY USSD"
        texts.removeAt(0)
        texts.add(MESSAGE)
        every { accessibilityEvent.text } returns texts
        every { accessibilityEvent.source } returns null
        ussdController.ussdInterface = ussdInterface

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        MESSAGE = "Final Close dialog"
        texts.removeAt(0)
        texts.add(MESSAGE)
        every { USSDService.notInputText(any()) } returns true
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))
    }

    @Test
    fun callUSSDOverlayInvokeMultipleMessages() {
        val map = HashMap<String, HashSet<String>>()
        map["KEY_LOGIN"] = HashSet(Arrays.asList("espere", "waiting", "loading", "esperando"))
        map["KEY_ERROR"] = HashSet(Arrays.asList("problema", "problem", "error", "null"))
        mockkObject(USSDController)
        mockkObject(USSDController.Companion)
        mockkObject(USSDService)
        mockkObject(USSDService.Companion)
        mockkStatic(Uri::class)

        every { USSDController.verifyAccesibilityAccess(any()) } returns true
        every { USSDController.verifyOverLay(any()) } returns true
        every { Uri.decode(any()) } returns ""
        every { Uri.parse(any()) } returns uri
        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`(activity).startActivity(any())

        every { accessibilityEvent.className } returns "amigo.app.AmigoAlertDialog"
        val texts = object : ArrayList<CharSequence>() {}
        var MESSAGE = "waiting"
        texts.add(MESSAGE)
        every { accessibilityEvent.text } returns texts
        every { accessibilityEvent.source } returns null
        ussdController.ussdInterface = ussdInterface

        ussdController.callUSSDOverlayInvoke("*1#", map, callbackInvoke!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        doAnswer {
            ussdService.onAccessibilityEvent(accessibilityEvent!!)
            null
        }.`when`(ussdInterface).sendData(any())

        MESSAGE = "Return a message from GATEWAY USSD"
        texts.removeAt(0)
        texts.add(MESSAGE)
        every { accessibilityEvent.text } returns texts
        every { accessibilityEvent.source } returns null
        ussdController.ussdInterface = ussdInterface

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        ussdController.send("1", callbackMessage!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))

        MESSAGE = "Final Close dialog"
        texts.removeAt(0)
        texts.add(MESSAGE)
        every { USSDService.notInputText(any()) } returns true
        ussdController.callUSSDInvoke("*1#", map, callbackInvoke!!)
        verify { callbackInvoke.over(capture(stringArgumentCaptor)) }
        assertThat(stringArgumentCaptor.captured, `is`(equalTo(MESSAGE)))
    }

}