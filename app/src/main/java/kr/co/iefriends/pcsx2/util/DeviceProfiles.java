
/*

Originally from ARMSX2 by MoonPower (Momo-AUX1) - GPLv3 License
   This file is part of RETROps2, a fork of ARMSX2.

   RETROps2 is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   RETROps2 is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with RETROps2.  If not, see <http://www.gnu.org/licenses/>.

*/

package kr.co.iefriends.pcsx2.util;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import kr.co.iefriends.pcsx2.R;

public final class DeviceProfiles {
    private static final String FEATURE_SAMSUNG_DEX = "com.samsung.android.feature.SEM_DESKTOP_MODE";
    private static final String FEATURE_SAMSUNG_DEX_ALT = "com.samsung.android.feature.CONTROL_DEX";
    private static final String FEATURE_GOOGLE_DESKTOP = PackageManager.FEATURE_PC;
    private static final String FEATURE_CHROME_OS = "org.chromium.arc";

    private DeviceProfiles() {}

    public static boolean isAndroidTV(Context context) {
        if (context == null) return false;
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager == null) return false;
        int mode = uiModeManager.getCurrentModeType();
        return mode == Configuration.UI_MODE_TYPE_TELEVISION || uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_APPLIANCE;
    }

    public static boolean isSamsungDex(Context context) {
        if (context == null) return false;
        PackageManager pm = context.getPackageManager();
        if (pm == null) return false;
        boolean hasDexFeature = pm.hasSystemFeature(FEATURE_SAMSUNG_DEX) || pm.hasSystemFeature(FEATURE_SAMSUNG_DEX_ALT);
        if (!hasDexFeature) return false;
        // Some Samsung devices report the feature even when Dex is not active. If the manufacturer isn't Samsung, bail out.
        return TextUtils.equals(Build.MANUFACTURER, "samsung") || TextUtils.equals(Build.BRAND, "samsung");
    }

    public static boolean isChromebook(Context context) {
        if (context == null) return false;
        PackageManager pm = context.getPackageManager();
        if (pm == null) return false;
        return pm.hasSystemFeature(FEATURE_GOOGLE_DESKTOP) || pm.hasSystemFeature(FEATURE_CHROME_OS);
    }

    public static boolean isDesktopExperience(Context context) {
        return isSamsungDex(context) || isChromebook(context);
    }

    public static boolean isTvOrDesktop(Context context) {
        return isAndroidTV(context) || isDesktopExperience(context);
    }

    public static boolean isTouchOptimized(Context context) {
        return !isTvOrDesktop(context);
    }

    public static String getProductDisplayName(Context context, String defaultName) {
        if (context == null) {
            return defaultName;
        }
        if (isAndroidTV(context)) {
            return context.getString(R.string.app_name_tv);
        }
        if (isDesktopExperience(context)) {
            return context.getString(R.string.app_name_desktop);
        }
        return defaultName;
    }

    /**
     * Detects if the device is using a Qualcomm Adreno GPU
     */
    public static boolean isAdrenoGPU() {
        try {
            String glRenderer = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_RENDERER);
            if (glRenderer != null) {
                return glRenderer.toLowerCase().contains("adreno");
            }
        } catch (Exception e) {
            // Fallback: check SoC model
            String hardware = Build.HARDWARE.toLowerCase();
            String board = Build.BOARD.toLowerCase();
            return hardware.contains("qcom") || hardware.contains("qualcomm") ||
                   board.contains("kalama") || board.contains("pineapple");
        }
        return false;
    }

    /**
     * Detects if the device is using Qualcomm G3x Gen 2 (Adreno 740)
     * This SoC is known to have specific Vulkan compatibility issues
     */
    public static boolean isQualcommG3xGen2() {
        String hardware = Build.HARDWARE.toLowerCase();
        String board = Build.BOARD.toLowerCase();
        String soc = Build.SOC_MODEL != null ? Build.SOC_MODEL.toLowerCase() : "";

        // G3x Gen 2 uses "kalama" codename (SM8550 variant)
        return hardware.contains("kalama") || board.contains("kalama") ||
               soc.contains("sm8550") || soc.contains("g3x");
    }

    /**
     * Returns recommended renderer for the current device
     * @return "opengl" or "vulkan"
     */
    public static String getRecommendedRenderer() {
        if (isQualcommG3xGen2()) {
            // G3x Gen 2 has better OpenGL ES compatibility
            android.util.Log.i("RETROps2", "Detected Qualcomm G3x Gen 2 - recommending OpenGL renderer");
            return "opengl";
        }
        if (isAdrenoGPU()) {
            // Older Adreno GPUs may have Vulkan issues
            android.util.Log.i("RETROps2", "Detected Adreno GPU - recommending OpenGL renderer");
            return "opengl";
        }
        // Default to Vulkan for other devices
        return "vulkan";
    }
}
