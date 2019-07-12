/* ----------------------------------------------------------------- */
/* CODE FOR BUILDING LIBRARY OF FLITE-HTS ENGINE  TO USE ON ANDROID  */
/*           developed by Indian Language Text-to-Speech Consortium  */
/* ----------------------------------------------------------------- */
/*                                                                   */
/*  Copyright (c) 2015  Indian Language Text-to-Speech Consortium    */ 
 /*                     Headed by Prof Hema A Murthy, IIT Madras     */  
/*                      Department of Computer Science & Engineering */ 
 /*                     hema@cse.iitm.ac.in                          */
/*                                                                   */
/*                               				     */
/* All rights reserved.                                              */
/*                                                                   */
/* Redistribution and use in source and binary forms, with or        */
/* without modification, are permitted provided that the following   */
/* conditions are met:                                               */
/*                                                                   */
/* - It can be used for research purpose but for commercial use      */ 
/*     prior premission is needed.		                     */
/* -Redistributions of source code must retain the above copyright   */
/*   notice, this list of conditions and the following disclaimer.   */
/* - Redistributions in binary form must reproduce the above         */
/*   copyright notice, this list of conditions and the following     */
/*   disclaimer in the documentation and/or other materials provided */
/*   with the distribution.                                          */
/* - Neither the name of the Indian Language TTS Consortium nor      */
/*     the names of its  					     */
/*   contributors may be used to endorse or promote products derived */
/*   from this software without specific prior written permission.   */
/*                                                                   */
/* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND            */
/* CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,       */
/* INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF          */
/* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE          */
/* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS */
/* BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,          */
/* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED   */
/* TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,     */
/* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON */
/* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,   */
/* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY    */
/* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE           */
/* POSSIBILITY OF SUCH DAMAGE.                                       */
/* ----------------------------------------------------------------- */

#include"com_mslabiitm_iitmflitehtshindi_MainActivity.h"
#include"my_flite_hts.h"
#include <jni.h>
#include <string.h>


/*
 * Class:     com_speechlabssn_ssnflitehtstamil_MainActivity
 * Method:    mainfn
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_vinay_screenreader_MyScreenReaderService_mainfn
  (JNIEnv *env, jobject obj, jstring Usertext_cont,jstring path,jstring wpath)
  {
PRINTMSG("***Before Synthesis***\n");
        //jclass userClass = (*env)->GetObjectClass(env, userobject);
        //jmethodID getUsertext_cont = (*env)->GetMethodID(env, userClass, "getUsertext", "()I");
        //char* text =  (char*)(*env)->CallIntMethod(env, userobject, getUsertext_cont);
        const char *aa = (*env)->GetStringUTFChars(env, Usertext_cont, 0);
        const char *bb = (*env)->GetStringUTFChars(env, path, 0);
        const char *cc = (*env)->GetStringUTFChars(env, wpath, 0);
        char * text = strdup(aa);
        char * filepath = strdup(bb);
        char * wavpath = strdup(cc);
        int y = main_syn(text,filepath,wavpath);
PRINTMSG("***text***%s\n",text);
PRINTMSG("***file***%s\n",filepath);
PRINTMSG("***wav***%s\n",wavpath);

        int x = num_return();
        if(y==-1)
        {
PRINTMSG("***I am here***\n");
            (*env)->NewStringUTF(env,"got -1");
        }
        else
        {
PRINTMSG("HELLOJNI\n");
            (*env)->NewStringUTF(env,aa);
        }

  }
