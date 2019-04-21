package com.romellfudi.ussdlibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import java.util.*

/**
 * @author Romell Dominguez
 * @version 1.1.i 2019/04/18
 * @since 1.1.i
 */
class USSDController private constructor(var context: Context) : USSDInterface, USSDApi {

    var map: HashMap<String, HashSet<String>>? = null

    lateinit var callbackInvoke: CallbackInvoke

    lateinit var callbackMessage: (String) -> Unit

    var isRunning: Boolean? = false

    var ussdInterface: USSDInterface? = null

    init {
            ussdInterface = this
    }

    /**
     * Invoke a dial-up calling a ussd number
     *
     * @param ussdPhoneNumber ussd number
     * @param map             Map of Login and problem messages
     * @param callbackInvoke  a callback object from return answer
     */
    override fun callUSSDInvoke(ussdPhoneNumber: String, map: HashMap<String, HashSet<String>>, callbackInvoke: CallbackInvoke) {
        callUSSDInvoke(ussdPhoneNumber, 0, map, callbackInvoke)
    }

    /**
     * Invoke a dial-up calling a ussd number and
     * you had a overlay progress widget
     *
     * @param ussdPhoneNumber ussd number
     * @param map             Map of Login and problem messages
     * @param callbackInvoke  a callback object from return answer
     */
    override fun callUSSDOverlayInvoke(ussdPhoneNumber: String, map: HashMap<String, HashSet<String>>, callbackInvoke: CallbackInvoke) {
        callUSSDOverlayInvoke(ussdPhoneNumber, 0, map, callbackInvoke)
    }

    /**
     * Invoke a dial-up calling a ussd number
     *
     * @param ussdPhoneNumber ussd number
     * @param simSlot         simSlot number to use
     * @param map             Map of Login and problem messages
     * @param callbackInvoke  a callback object from return answer
     */
    @SuppressLint("MissingPermission")
    override fun callUSSDInvoke(ussdPhoneNumber: String, simSlot: Int, map: HashMap<String, HashSet<String>>, callbackInvoke: CallbackInvoke) {
        this.callbackInvoke = callbackInvoke
        this.map = map
        if (verifyAccesibilityAccess(context)) {
            dialUp(ussdPhoneNumber, simSlot)
        } else {
            this.callbackInvoke.over("Check your accessibility")
        }
    }

    /**
     * Invoke a dial-up calling a ussd number and
     * you had a overlay progress widget
     *
     * @param ussdPhoneNumber ussd number
     * @param simSlot         simSlot number to use
     * @param map             Map of Login and problem messages
     * @param callbackInvoke  a callback object from return answer
     */
    @SuppressLint("MissingPermission")
    override fun callUSSDOverlayInvoke(ussdPhoneNumber: String, simSlot: Int, map: HashMap<String, HashSet<String>>, callbackInvoke: CallbackInvoke) {
        this.callbackInvoke = callbackInvoke
        this.map = map
        if (verifyAccesibilityAccess(context) && verifyOverLay(context)) {
            dialUp(ussdPhoneNumber, simSlot)
        } else {
            this.callbackInvoke.over("Check your accessibility | overlay permission")
        }
    }

    /**
     * Invoke a dial-up calling a ussd number and
     * you had a overlay progress widget
     *
     * @param ussdPhoneNumber ussd number
     * @param simSlot         simSlot number to use
     *
     */
    private fun dialUp(ussdPhoneNumber: String, simSlot: Int) {
        var ussdPhoneNumber = ussdPhoneNumber
        if (map == null || !map!!.containsKey(KEY_ERROR) || !map!!.containsKey(KEY_LOGIN)) {
            this.callbackInvoke.over("Bad Mapping structure")
            return
        }
        if (ussdPhoneNumber.isEmpty()) {
            this.callbackInvoke.over("Bad ussd number")
            return
        }
        val uri = Uri.encode("#")
        if (uri != null) {
            ussdPhoneNumber = ussdPhoneNumber.replace("#", uri)
        }
        val uriPhone = Uri.parse("tel:$ussdPhoneNumber")
        if (uriPhone != null)
            isRunning = true
        this.context.startActivity(getActionCallIntent(uriPhone, simSlot))
    }

    /**
     * get action call Intent
     *
     * @param uri     parsed uri to call
     * @param simSlot simSlot number to use
     */
    @SuppressLint("MissingPermission")
    private fun getActionCallIntent(uri: Uri?, simSlot: Int): Intent {
        // https://stackoverflow.com/questions/25524476/make-call-using-a-specified-sim-in-a-dual-sim-device
        val simSlotName = arrayOf("extra_asus_dial_use_dualsim", "com.android.phone.extra.slot", "slot", "simslot", "sim_slot", "subscription", "Subscription", "phone", "com.android.phone.DialingMode", "simSlot", "slot_id", "simId", "simnum", "phone_type", "slotId", "slotIdx")

        val intent = Intent(Intent.ACTION_CALL, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("com.android.phone.force.slot", true)
        intent.putExtra("Cdma_Supp", true)

        for (s in simSlotName)
            intent.putExtra(s, simSlot)

        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE)
        if (telecomManager != null) {
            val telecomManager = telecomManager as TelecomManager
            val phoneAccountHandleList = telecomManager.callCapablePhoneAccounts
            if (phoneAccountHandleList != null && phoneAccountHandleList.size > simSlot)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList[simSlot])
        }

        return intent
    }

    override fun sendData(text: String) {
        USSDService.send(text)
    }

    override fun send(text: String, callbackMessage: (String) -> Unit) {
        this.callbackMessage = callbackMessage
        ussdInterface?.sendData(text)
    }

    /**
     * Invoke class to comunicate messages between USSD and App
     */
    interface CallbackInvoke {
        fun responseInvoke(message: String)

        fun over(message: String)
    }

    // Unit Kotlin instead of Interface with just one message
    interface CallbackMessage {
        fun responseMessage(message: String)
    }

    companion object {

        // singleton reference
        var instance: USSDController? = null

        val KEY_LOGIN = "KEY_LOGIN"

        val KEY_ERROR = "KEY_ERROR"

        /**
         * The Singleton building method
         *
         * @param context An activity that could call
         * @return An instance of USSDController
         */
        fun getInstance(context: Context): USSDController {
            if (instance == null)
                instance = USSDController(context)
            return instance as USSDController
        }

        fun verifyAccesibilityAccess(context: Context): Boolean {
            val isEnabled = isAccessiblityServicesEnable(context)
            if (!isEnabled) {
                if (context is Activity) {
                    openSettingsAccessibility(context)
                } else {
                    Toast.makeText(
                            context, "voipUSSD accessibility service is not enabled",
                            Toast.LENGTH_LONG).show()
                }
            }
            return isEnabled
        }

        fun verifyOverLay(context: Context): Boolean {
            val notGrant = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(context)
            return if (notGrant) {
                if (context is Activity) {
                    openSettingsOverlay(context)
                } else {
                    Toast.makeText(context,
                            "Overlay permission have not grant permission.",
                            Toast.LENGTH_LONG).show()
                }
                false
            } else
                true
        }

        private fun openSettingsAccessibility(activity: Activity) {
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle("USSD Accessibility permission")
            val name = getNameApp(activity)
            alertDialogBuilder
                    .setMessage("You must enable accessibility permissions for the app '$name'")
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setNeutralButton("ok") { dialog, id -> activity.startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 1) }
            val alertDialog = alertDialogBuilder.create()
            alertDialog?.show()
        }

        private fun openSettingsOverlay(activity: Activity) {
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle("USSD Overlay permission")
            val name = getNameApp(activity)
            alertDialogBuilder
                    .setMessage("You must allow for the app to appear '$name' on top of other apps.")
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setNeutralButton("ok") { dialog, id ->
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.packageName))
                activity.startActivity(intent)
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog?.show()
        }

        private fun getNameApp(activity: Activity): String{
            val applicationInfo = activity.applicationInfo
            val stringId = applicationInfo.labelRes
            return if (applicationInfo.labelRes == 0)
                applicationInfo.nonLocalizedLabel.toString()
            else
                activity.getString(stringId)
        }


        protected fun isAccessiblityServicesEnable(context: Context): Boolean {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
            if (am != null) {
                val am = am as AccessibilityManager
                for (service in am.installedAccessibilityServiceList) {
                    if (service.id.contains(context.packageName)) {
                        return isAccessibilitySettingsOn(context, service.id)
                    }
                }
            }
            return false
        }

        protected fun isAccessibilitySettingsOn(context: Context, service: String): Boolean {
            var accessibilityEnabled = 0
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                        context.applicationContext.contentResolver,
                        Settings.Secure.ACCESSIBILITY_ENABLED)
            } catch (e: Settings.SettingNotFoundException) {
                //
            }

            if (accessibilityEnabled == 1) {
                val settingValue = Settings.Secure.getString(
                        context.applicationContext.contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                if (settingValue != null) {
                    val splitter = TextUtils.SimpleStringSplitter(':')
                    splitter.setString(settingValue)
                    while (splitter.hasNext()) {
                        val accessabilityService = splitter.next()
                        if (accessabilityService == service) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}
