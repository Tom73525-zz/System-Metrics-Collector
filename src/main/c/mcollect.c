#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include "org_robert_tom_App.h"


JNIEXPORT jlong JNICALL Java_org_robert_tom_App_getSystemClockInTicks (JNIEnv *env, jobject obj) {
    jlong tocks = sysconf(_SC_CLK_TCK);
    return tocks;
}