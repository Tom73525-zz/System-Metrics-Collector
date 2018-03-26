#include <jni.h>
#include <stdio.h>
#include <dirent.h>
#include <ctype.h>
#include <string.h>
#include "org_robert_tom_App.h"

//private native void pidList();

typedef enum { false, true } bool;

bool digit_check(const char* proc_dir){
  const int string_len = strlen(proc_dir);
  for(int i = 0; i < string_len; i++){
    if(!isdigit(proc_dir[i]))
      return false;
  }
  return true;
}

JNIEXPORT void JNICALL Java_org_robert_tom_App_pidList(JNIEnv *env, jobject thisObj) {
	DIR *d;
	  struct dirent *dir;
	  d = opendir("/proc");
	  if(d) {
	    while((dir = readdir(d)) != NULL) {
	      if(digit_check(dir->d_name)){
	        printf("%s\n", dir->d_name);
	      }
	    }
	    closedir(d);
	  }

	  fflush(stdout);
	  return;
}