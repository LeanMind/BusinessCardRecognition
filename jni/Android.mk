LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include C:\OpenCV4Android\OpenCV-2.4.6-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE    := cardreader
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)