package com.lt2333.simplicitytools.hook.app.securitycenter

import com.lt2333.simplicitytools.util.XSPUtils
import com.lt2333.simplicitytools.util.xposed.base.HookRegister
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object RemoveMacroBlacklist : HookRegister() {

    override fun init() {
        if (XSPUtils.getBoolean("remove_macro_blacklist", false)) {
            var letter = 'a'
            for (i in 0..25) {
                val classIfExists = XposedHelpers.findClassIfExists(
                    "com.miui.gamebooster.v.$letter" + "0", getDefaultClassLoader()
                ) ?: continue
                if (classIfExists.declaredMethods.size in 6..12 && classIfExists.fields.isEmpty() && classIfExists.declaredFields.size >= 2) {
                    XposedHelpers.findAndHookMethod(classIfExists, "c", String::class.java,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.result = false
                            }
                        })
                    return
                }
                letter++
            }
        }
    }

}