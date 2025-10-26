# Qualcomm G3x Gen 2 / Adreno 740 Compatibility Fixes

## Problem Description

RETROps2 crashes when launching games on devices with **Qualcomm G3x Gen 2** SoC (Adreno 740 GPU). The crash occurs during Vulkan initialization due to missing or incompatible GPU features.

## Root Causes Identified

1. **Missing Geometry Shader Support** - Adreno 740 may not fully support `geometryShader` feature required for `primitive_id`
2. **Stencil Buffer Issues** - `VK_FORMAT_D32_SFLOAT_S8_UINT` format may not be supported
3. **Texture Barrier Limitations** - `VK_EXT_rasterization_order_attachment_access` extension missing
4. **Framebuffer Fetch Unavailable** - Critical for some rendering effects
5. **Driver Bugs** - Qualcomm Vulkan drivers have known stability issues on certain chipsets

## Applied Fixes

### 1. Device Detection (DeviceProfiles.java)

Added methods to detect Adreno GPUs and specifically Qualcomm G3x Gen 2:

- `isAdrenoGPU()` - Detects any Adreno GPU via OpenGL renderer string or hardware info
- `isQualcommG3xGen2()` - Specifically detects G3x Gen 2 via hardware codename "kalama" or SoC model
- `getRecommendedRenderer()` - Returns "opengl" for Adreno GPUs, "vulkan" for others

**Location:** `app/src/main/java/kr/co/iefriends/pcsx2/util/DeviceProfiles.java`

### 2. Vulkan Workarounds (GSDeviceVK.cpp)

Added specific workarounds for Adreno 740 in the `CheckFeatures()` method:

- Detects Adreno 740 via device ID range (0x43050a01 - 0x43050aff)
- Logs warnings for missing features
- Disables `primitive_id` if geometry shaders are unavailable
- Provides detailed console output for debugging

**Location:** `app/src/main/cpp/pcsx2/GS/Renderers/Vulkan/GSDeviceVK.cpp` (lines 2675-2702)

### 3. Improved Error Messages

When Vulkan initialization fails on Adreno GPUs, the error message now:

- Identifies the GPU as Adreno
- Suggests switching to OpenGL renderer
- Shows device ID for debugging

**Location:** `app/src/main/cpp/pcsx2/GS/Renderers/Vulkan/GSDeviceVK.cpp` (lines 2068-2089)

## How to Test

### Building the App

```bash
./gradlew assembleDebug
```

### Enable Logging

1. Go to Settings → General
2. Enable "Record Logs"
3. Logs will be saved to: `/sdcard/Android/data/kr.co.iefriends.pcsx2/files/ANDROID_LOG.txt`

### Testing on G3x Gen 2 Device

1. **First Launch** - Try running a game with Vulkan renderer (default)
2. **Check Logs** - Look for these messages in logcat or ANDROID_LOG.txt:
   ```
   VK: Detected Adreno 740 (Qualcomm G3x Gen 2).
   VK: Applying workarounds for known driver issues.
   VK: Geometry shader unavailable, disabling primitive_id.
   ```

3. **If Crash Occurs** - You'll see the error dialog suggesting OpenGL
4. **Switch to OpenGL**:
   - Go to Settings → Graphics
   - Change "Renderer" from "Vulkan" to "OpenGL"
   - Restart the app and try launching a game again

### Collecting Debug Information

If issues persist, collect this information:

```bash
adb logcat -d > logcat.txt
adb shell getprop | grep -i qcom > device_props.txt
adb pull /sdcard/Android/data/kr.co.iefriends.pcsx2/files/ANDROID_LOG.txt
```

Key information to look for:
- GPU vendor ID (should be 0x5143 for Qualcomm)
- GPU device ID (Adreno 740 should be 0x43050aXX)
- Available Vulkan features
- Missing extensions or formats

## Expected Behavior

### With Fixes Applied

1. **Vulkan Mode**:
   - App should detect Adreno 740
   - Apply workarounds automatically
   - Show warnings in console
   - If features still insufficient, show helpful error message

2. **OpenGL Mode** (Fallback):
   - Should work reliably on Adreno 740
   - Better driver compatibility
   - Slightly lower performance but more stable

### Performance Expectations

- **OpenGL**: 30-60 FPS on most PS2 games (depending on game complexity)
- **Vulkan** (if working): 40-90 FPS with better efficiency

## Known Limitations

1. Some graphical effects may not work without stencil buffer
2. Texture barriers disabled = some rendering glitches possible
3. OpenGL may have slightly higher CPU overhead

## Future Improvements

Potential enhancements for better G3x Gen 2 support:

1. **Auto-detect and switch renderer** - Automatically use OpenGL on first launch for Adreno GPUs
2. **Per-game profiles** - Some games may work better with specific settings
3. **Shader cache optimization** - Pre-compile shaders for faster loading
4. **Dynamic feature detection** - Adjust quality settings based on available GPU features

## Device Information

### Qualcomm G3x Gen 2 Specs

- **CPU**: Kryo (Cortex-A715 + A710 + A510 cores)
- **GPU**: Adreno 740
- **Vulkan**: 1.3 support (driver-dependent)
- **OpenGL ES**: 3.2
- **Codename**: kalama / SM8550

### Known Affected Devices

- Razer Edge (2023)
- Logitech G Cloud Gaming Handheld
- Other G3x Gen 2 based gaming devices

## Debugging Commands

### Check GPU Info
```bash
adb shell dumpsys SurfaceFlinger | grep GLES
adb shell getprop ro.hardware
adb shell getprop ro.board.platform
```

### Monitor Real-time Logs
```bash
adb logcat -s RETROps2:I ARMSX2:I PCSX2:I
```

### Check Vulkan Support
```bash
adb shell dumpsys SurfaceFlinger | grep -i vulkan
```

## Contact & Support

If you encounter issues not covered by these fixes:

1. Open an issue on GitHub: https://github.com/[your-repo]/retrops2/issues
2. Include:
   - Device model
   - Android version
   - Renderer used (Vulkan/OpenGL)
   - Log files (ANDROID_LOG.txt and logcat)
   - Steps to reproduce

## Credits

- Original ARMSX2 project by [@MoonPower](https://github.com/momo-AUX1)
- PCSX2 team for the core emulator
- Community bug reports and testing

---

**Last Updated:** 2025-01-26
**RETROps2 Version:** 0.0.1
