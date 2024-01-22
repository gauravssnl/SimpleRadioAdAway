package com.gauravssnl.simpleradioadaway

import android.os.Bundle
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import org.apache.commons.lang3.ClassUtils

private const val DEBUG = false //adding this flag to avoid flooding logs; but use for DEBUG
private lateinit var module: MainModule

class MainModule(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {
    init {
        log("MainModule at :: " + param.processName)
        module = this
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(param)
        log("onPackageLoaded :: ${param.packageName}")
        log("param classloader is ::  ${param.classLoader}")
        log("module apk path is :: ${this.applicationInfo.sourceDir}")
        log("----------------------------")
        if (param.isFirstPackage) {
            log("First package loaded")
            log("Try finding classes & hooking methods")
            val className1 = "${param.packageName}.SimpleRadioBaseActivity"
            log("Trying to find className :: $className1")
            val clazz1 = ClassUtils.getClass(param.classLoader, className1, false)
            val method1 = clazz1.getDeclaredMethod("initAds")
            hook(method1, MyHooker::class.java)
            val method2 = clazz1.getDeclaredMethod("preloadActivityAds")
            hook(method2, MyHooker::class.java)
            val method3 = clazz1.getDeclaredMethod("isPremium")
            hook(method3, MyHooker::class.java)
            val method4 = clazz1.getDeclaredMethod("getAdRequest")
            hook(method4, MyHooker::class.java)
            val method5 = clazz1.getDeclaredMethod("getAdManagerAdRequest")
            hook(method5, MyHooker::class.java)
            val method6 = clazz1.getDeclaredMethod("startCMPDeniedActivity")
            hook(method6, MyHooker::class.java)
            val method7 = clazz1.getDeclaredMethod("openIABScreen", String::class.java)
            hook(method7, MyHooker::class.java)
            val method8 = clazz1.getDeclaredMethod(
                "showInterstitial", String::class.java, String::class.java
            )
            hook(method8, MyHooker::class.java)
            val method9 = clazz1.getDeclaredMethod(
                "initInterstitialAd",
                String::class.java,
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType
            )
            hook(method9, MyHooker::class.java)

            val className2 = "${param.packageName}.MainActivity"
            log("Trying to find className :: $className2")
            val clazz2 = ClassUtils.getClass(param.classLoader, className2, false)
            val method10 = clazz2.getDeclaredMethod("createAdView")
            hook(method10, MyHooker::class.java)
            val method11 = clazz2.getDeclaredMethod("getInterstitialExperimentAdUnit")
            hook(method11, MyHooker::class.java)

            val method12 = clazz1.getDeclaredMethod("getInterstitialAdUnit")
            hook(method12, MyHooker::class.java)
            val method13 = clazz1.getDeclaredMethod("openIABScreen", String::class.java)
            hook(method13, MyHooker::class.java)
            val method14 = clazz1.getDeclaredMethod("startCMPDeniedActivity")
            hook(method14, MyHooker::class.java)
            val method15 =
                clazz1.getDeclaredMethod("showInterstitial", String::class.java, String::class.java)
            hook(method15, MyHooker::class.java)
            val method16 =
                clazz1.getDeclaredMethod("hideBannerAd", Boolean::class.javaPrimitiveType)
            hook(method16, MyHooker::class.java)

            val className3 = "${param.packageName}.experiment.AdsExperiment"
            log("Trying to find className :: $className2")
            val clazz3 = ClassUtils.getClass(param.classLoader, className3, false)
            val method17 = clazz3.getDeclaredMethod("b0")
            hook(method17, MyHooker::class.java)
            val method18 = clazz3.getDeclaredMethod("S")
            hook(method18, MyHooker::class.java)
            val method19 = clazz3.getDeclaredMethod("Q")
            hook(method19, MyHooker::class.java)
            val method20 = clazz3.getDeclaredMethod("A0")
            hook(method20, MyHooker::class.java)
            val method21 = clazz3.getDeclaredMethod("N1")
            hook(method21, MyHooker::class.java)

            val method24 = clazz1.getDeclaredMethod("onPostCreate", Bundle::class.java)
            hook(method24, MyHooker::class.java)

            log("All hooking completed. Enjoy Ad-free experience")
        }
    }

    @XposedHooker
    class MyHooker : XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeInvocation(callback: BeforeHookCallback): MyHooker {
                if (DEBUG) module.log("beforeInvocation callback params :: ${callback.args.contentToString()}")
                if (DEBUG) module.log("callback member :: ${callback.member}")
                if ("hideBannerAd".contentEquals(callback.member.name)) callback.args[0] = true
                return MyHooker()
            }

            @JvmStatic
            @AfterInvocation
            fun afterInvocation(callback: AfterHookCallback, context: MyHooker) {
                if (DEBUG) module.log("afterInvocation callback params :: ${callback.args.contentToString()}")
                if (DEBUG) module.log("callback member name :: ${callback.member.name}")
                callback.result = when (callback.member.name) {
                    "isPremium", "b0" -> true
                    "S", "A0", "Q" -> false
                    "getAdRequest", "getInterstitialExperimentAdUnit", "getInterstitialAdUnit" -> null
                    "onPostCreate" -> {
                        val activityClazzObj = callback.thisObject
                        val mHideAdField =
                            activityClazzObj!!::class.java.superclass.getDeclaredField("mHideAd")
                        mHideAdField.isAccessible = true
                        mHideAdField.setBoolean(activityClazzObj, true)

                        val mHideAdFmAdAdapterField =
                            activityClazzObj::class.java.superclass.getDeclaredField("mAdAdapter")
                        mHideAdFmAdAdapterField.isAccessible = true
                        mHideAdFmAdAdapterField.set(activityClazzObj, null)

                        val mAdsManagerField =
                            activityClazzObj::class.java.superclass.getDeclaredField("mMediaLabAdViewContainer")
                        mAdsManagerField.isAccessible = true
                        mAdsManagerField.set(activityClazzObj, null)
                    }

                    else -> return
                }
            }
        }
    }
}