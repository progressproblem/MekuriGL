#include <jni.h>
#include <GLES/gl.h>
#include <stdio.h>
#include <stdlib.h>
#include "NDKImage.hpp"

JNIEXPORT void JNICALL Java_com_example_graphic_TextureLoader_texImage2DNonPremultipliedAlpha
  (JNIEnv* env, jclass thiz, jintArray pixelArray, jint width, jint height){
    unsigned int* pixels = (unsigned int*)env->GetIntArrayElements(pixelArray, (jboolean*)NULL);

    for (int i = 0; i < width * height; i++)
    {
        unsigned int p = pixels[i];
        // ABGR -> ARGB なんでABGRになってるのかはよくわからん･･･
        pixels[i] =
            (((p      ) & 0xFF000000) | // A
             ((p << 16) & 0x00FF0000) | // R
             ((p      ) & 0x0000FF00) | // G
             ((p >> 16) & 0x000000FF)); // B
    }

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

    env->ReleaseIntArrayElements(pixelArray, (int*)pixels, JNI_ABORT);
}
