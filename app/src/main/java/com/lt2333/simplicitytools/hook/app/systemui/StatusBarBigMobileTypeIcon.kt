package com.lt2333.simplicitytools.hook.app.systemui

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.lt2333.simplicitytools.util.XSPUtils
import com.lt2333.simplicitytools.util.hasEnable
import com.lt2333.simplicitytools.util.hookAfterMethod
import com.lt2333.simplicitytools.util.hookBeforeMethod
import com.lt2333.simplicitytools.util.xposed.base.HookRegister
import de.robv.android.xposed.XposedHelpers


object StatusBarBigMobileTypeIcon: HookRegister() {

    private val upAndDownPosition = XSPUtils.getInt("big_mobile_type_icon_up_and_down_position", 0)
    private val leftAndRightMargin = XSPUtils.getInt("big_mobile_type_icon_left_and_right_margins", 0)
    private val isBold = XSPUtils.getBoolean("big_mobile_type_icon_bold", true)
    private val size = XSPUtils.getFloat("big_mobile_type_icon_size", 12.5f)

    override fun init() {
        hasEnable("big_mobile_type_icon") {
            "com.android.systemui.statusbar.StatusBarMobileView".hookAfterMethod(
                getDefaultClassLoader(),
                "init"
            ) {
                val statusBarMobileView = it.thisObject as ViewGroup
                val context: Context = statusBarMobileView.context
                val res: Resources = context.resources

                //获取组件
                val mobileContainerLeftId: Int =
                    res.getIdentifier("mobile_container_left", "id", "com.android.systemui")
                val mobileContainerLeft =
                    statusBarMobileView.findViewById<ViewGroup>(mobileContainerLeftId)

                val mobileTypeId: Int =
                    res.getIdentifier("mobile_type", "id", "com.android.systemui")
                val mobileType = statusBarMobileView.findViewById<TextView>(mobileTypeId)

                val mobileLeftMobileInoutId: Int = res.getIdentifier(
                    "mobile_left_mobile_inout",
                    "id",
                    "com.android.systemui"
                )
                val mobileLeftMobileInout =
                    statusBarMobileView.findViewById<ImageView>(mobileLeftMobileInoutId)


                //获取插入位置
                val mobileContainerRightId: Int = res.getIdentifier(
                    "mobile_container_right",
                    "id",
                    "com.android.systemui"
                )
                val mobileContainerRight =
                    statusBarMobileView.findViewById<ViewGroup>(mobileContainerRightId)
                val rightParentLayout = mobileContainerRight.parent as ViewGroup
                val mobileContainerRightIndex =
                    rightParentLayout.indexOfChild(mobileContainerRight)

                //创建新布局
                val newLinearLayoutLP = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).also {

                }
                val newLinearlayout = LinearLayout(context).also {
                    it.layoutParams = newLinearLayoutLP
                    it.id = mobileContainerLeftId
                    it.setPadding(leftAndRightMargin, 0, leftAndRightMargin, 0)
                }
                XposedHelpers.setObjectField(it.thisObject, "mMobileLeftContainer", newLinearlayout)
                rightParentLayout.addView(
                    newLinearlayout,
                    mobileContainerRightIndex
                )

                //将组件插入新的布局
                (mobileType.parent as ViewGroup).removeView(mobileType)
                (mobileLeftMobileInout.parent as ViewGroup).removeView(mobileLeftMobileInout)
                (mobileContainerLeft.parent as ViewGroup).removeView(mobileContainerLeft)


                newLinearlayout.addView(mobileType)    //类型
                val mobileTypeLp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.gravity = Gravity.CENTER_VERTICAL
                    it.topMargin = upAndDownPosition
                }
                mobileType.also {
                    it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
                    if (isBold) {
                        it.typeface = Typeface.DEFAULT_BOLD
                    }
                    it.layoutParams = mobileTypeLp
                }


                newLinearlayout.addView(mobileLeftMobileInout)   //箭头
                val mobileLeftMobileInoutLp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                mobileLeftMobileInout.also {
                    it.layoutParams = mobileLeftMobileInoutLp
                }

                //屏蔽更新布局
                "com.android.systemui.statusbar.StatusBarMobileView".hookBeforeMethod(
                    getDefaultClassLoader(),
                    "updateMobileTypeLayout", String::class.java
                ) {
                    it.result = null
                }
            }
        }
    }

}