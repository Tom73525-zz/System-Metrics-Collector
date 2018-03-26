#include <stdio.h>
#include "JniSample.h"

JNIEXPORT jint JNICALL Java_JniSample_sayHellofromC (JNIEnv *env, jobject obj) {
  printf("Hello World from C !!\n");
  return 0;
}
