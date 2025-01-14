package com.lt2333.simplicitytools.util

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookMethod
import com.lt2333.simplicitytools.util.BaseHook
import de.robv.android.xposed.XposedBridge


object GetMinimumSignatureSchemeVersionForTargetSdk : BaseHook() {
    override fun init() {
        try {
            findMethod("android.util.apk.ApkSignatureVerifier") {
                name == "getMinimumSignatureSchemeVersionForTargetSdk"
            }.hookMethod {
                after { param ->
                    param.result = 0
                }
            }
            XposedBridge.log("BypassCheck: Hook getMinimumSignatureSchemeVersionForTargetSdk success!")
        } catch (e: Throwable) {
            XposedBridge.log("BypassCheck: Hook getMinimumSignatureSchemeVersionForTargetSdk failed!")
        }

    }

}
