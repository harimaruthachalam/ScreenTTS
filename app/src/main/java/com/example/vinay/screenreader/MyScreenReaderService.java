package com.example.vinay.screenreader;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.ActionBar;
import android.content.Intent;
import android.app.Service;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.content.res.AssetManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Deque;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


import static android.content.ContentValues.TAG;


public class MyScreenReaderService extends AccessibilityService implements TextToSpeech.OnInitListener {
    private final static String TAG = "MyScreenReaderService";
    private final int  ENG = 0;
    private final int  TEL = 1;
    private static MediaPlayer mediaPlayer ;
    private TextToSpeech tts;
    boolean mTtsInitialized = false;
    int reading_type = 0; //default mode
    String last;
    public String[] lines ;
    public String[] paras ;
    int nParas=0,nLines = 0;
    int curr_line = 0, curr_para=0;
    int curr_lang = ENG;
    boolean stopSpeechFlag = false; // flag will stop speech generation
    public static boolean mp_free = true; // flag for media player
    private int tel_curr_line = 0;
    private int tel_curr_line2 = 0;
    //String inputtext;

    //Configure the Accessibility Service
    @Override
    protected void onServiceConnected() {
        Toast.makeText(getApplication(), "ServiceConnected", Toast.LENGTH_SHORT).show();
        Log.v(TAG,"ServiceConnected");
Log.v(TAG,"****curr_lang****"+ " " + curr_lang);
        //Init TextToSpeech
        tts = new TextToSpeech(getApplicationContext(),this);
        mediaPlayer = new MediaPlayer();
        if (!PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext())
                .getBoolean("installed", false)) {
            PreferenceManager.getDefaultSharedPreferences(
                    getApplicationContext())
                    .edit().putBoolean("installed", true).apply();
            copyAssests();
            Log.v(TAG,"Copied");

        }
    }

    @Override
    /*
        obtain the text from various types of Accessibility events
        triggered by the application upon interaction by the user
     */
    public void onAccessibilityEvent(AccessibilityEvent event) {
       /* AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
        Log.v("Required GGGG --- ", source.toString());
        List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId = source.findAccessibilityNodeInfosByViewId("YOUR PACKAGE NAME:id/RESOURCE ID FROM WHERE YOU WANT DATA");
        if (findAccessibilityNodeInfosByViewId.size() > 0) {
            AccessibilityNodeInfo parent = (AccessibilityNodeInfo) findAccessibilityNodeInfosByViewId.get(0);
            // You can also traverse the list if required data is deep in view hierarchy.
            String requiredText = parent.getText().toString();
            Log.v("Required Text --- ", requiredText);
        }*/

	Log.v(TAG, "EVENT"+ event);

//        if(AccessibilityEvent.eventTypeToString(event.getEventType()).contains("WINDOW")) {
//            AccessibilityNodeInfo nodeInfo = event.getSource();
//            Toast.makeText(getApplicationContext(),nodeInfo.getText(),Toast.LENGTH_SHORT).show();
//        }
        final int eventType = event.getEventType();

        String eventText = "";
        String curr;
        Boolean speakFlag = false;
        Log.v(TAG,String.format(
                "onAccessibilityEvent: [type] %s [class] %s  [package]  %s [time] %s \n [text] %s \n [description] %s"
                ,getEventType(event),event.getClassName(),event.getPackageName(),event.getEventTime()
                ,event.getText().toString().trim(),event.getContentDescription()));
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_HOVER_ENTER) {
            //Do something, eg getting packagename
            final String packagename = String.valueOf(event.getPackageName());
            Log.v(TAG,"packagename"+" "+packagename);
        }
//        Log.v(TAG,String.format(
//                "onAccessibilityEvent: [type] %s [class] %s  [package]  %s [time] %s \n [text] %s \n [description] %s"
//                ,"TYPE_TOUCH_INTERACTION_START","android.webkit.webview","com.example.vinay.epubscreenreader",event.getEventTime()
//                ,event.getText().toString().trim(),event.getContentDescription()));


        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                speakFlag = true;
                Log.v(TAG, "CLicked");
                String tempo ;
                tempo = event.getText().toString();
		Log.v(TAG, "TEMPO"+ tempo);
                if(tempo.equals("[]")){

                }
                else
                    eventText = eventText + tempo;
//Log.v(TAG, "!!!eventText!!!!"+ " " + eventText);
Log.v(TAG, "tempo"+ " " + tempo);
                tts.speak(eventText, TextToSpeech.QUEUE_FLUSH, null,null);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
Log.v(TAG, "Changed");
                speakFlag = false;
                break;
           /* case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                speakFlag = true;
                Log.v(TAG, "i am TYPE_TOUCH_INTERACTION_START");
                break;*/
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                speakFlag = true;
//Log.v(TAG, "i am not clicked");
                if(event.getText().toString().equals("[]")){
                    Log.v(TAG, "i am in IF");
                   // Log.v(TAG,"event.getText().toString()"+" "+event.getText().toString());
                    if(event.getContentDescription() != null) {
                        String temp;

                        byte[] bytes;
                        temp = event.getContentDescription().toString();
                        if(curr_lang == ENG)
                            last = preProcess(temp);
                        else{

                            last = preProcess(temp);

                        }
                        curr_line = 0;
                        curr_para = 0;
                        split_lines(new String(last));
                        split_paras(new String(last));
                        Log.v("test", lines[0]);
                        Log.v("test", paras[0]);
                        curr_line = 0;
                        curr_para = 0;
                        eventText+= last;
                    }
                    else{
                        Log.v(TAG, "i am in ELSE");
                        switch(reading_type){
                            case 0:
                                eventText += last;
                                break;
                            case 1:
                                eventText += lines[curr_line];
                                break;
                            case 2:
                                eventText += paras[curr_para];
                                break;
                        }
                    }

//
//                    // Construct the formatted text
//                    String replacedWith = "<font color='red'>" + eventText + "</font>";
//
//                    // Get the text from TextView
//                    String originalString = event.getText().toString();
//
//                    // Replace the specified text/word with formatted text/word
//                    String modifiedString = originalString.replaceAll(eventText,replacedWith);
//
//                    // Update the TextView text
//                    event.setText(Html.fromHtml(modifiedString));

                    if(curr_lang == ENG)
                        tts.speak(eventText, TextToSpeech.QUEUE_FLUSH, null,null);
                    else if(curr_lang == TEL)
                        speak_telugu(eventText);
                }
                else{
Log.v(TAG,"curr_lang"+ " " + curr_lang);
                    String temp,temp0 ;
                    //temp0 = event.getBeforeText().toString();
                    temp0 = event.getText().toString().trim();
                    Log.v(TAG,"temp0****"+ " " + temp0);
                    Toast.makeText(getApplicationContext(), event + " Test!! ", Toast.LENGTH_SHORT).show();

                    temp = preProcessTelugu(temp0);
                    //eventText = eventText + temp;

                    Log.v(TAG,"check text0-"+ temp0);
                    Log.v(TAG,"check text "+ temp);
                    //read line by line
                    nLines = 0;
                    tel_curr_line2=0;
                    split_lines(temp);
                    if(curr_lang ==ENG) split_paras(temp0);
                    for (int i =0;i<nLines;i++){
                        //if(stopSpeechFlag)  break;
                        //speakAnyLanguage(lines[i]);


//                        //loop till mp is busy
//                        String textToHighlight = lines[i];
//
//                        // Construct the formatted text
//                        String replacedWith = "<font color='red'>" + textToHighlight + "</font>";
//
//                        // Get the text from TextView
//                        String originalString = event.getText().toString();
//
//                        // Replace the specified text/word with formatted text/word
//                        String modifiedString = originalString.replaceAll(textToHighlight,replacedWith);
//
//
//                        TextView wb = (TextView) event.getSource().findAccessibilityNodeInfosByViewId("contentView");
////                        AccessibilityNodeInfo n = event.getSource();
////                        Bundle arguments = new Bundle();
////                        arguments.putCharSequence(AccessibilityNodeInfo
////                                .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, modifiedString);
////                        n.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//
//                        // Update the TextView text
//                        wb.setText(modifiedString);
////                        wb.loadData(modifiedString,"text/html; charset=utf-8", "utf-8");
//
//                        stopSpeechFlag = false;
//                        speakAnyLanguage(lines[i]);
//                        Log.i(TAG,String.format("line - %d = %s ",i,lines[i]));
//                        //speakAnyLanguage(lines[i]);

                    }
                    stopSpeechFlag = false;
                    speakAnyLanguage(temp);
                }
                break;
            }

    }


    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt ");
        //
    }
    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        if (mTtsInitialized) {
            tts.stop();
            tts.shutdown();
        }
    }


    /*
        Initialise TTS and copy assets for flite HTS
     */
   /* public void onInit(int status) {
        Log.v(TAG, "TTsinit successfully");
        if(status == TextToSpeech.SUCCESS){
            Log.v(TAG, "Not successful");

            mTtsInitialized = true;
            tts.setLanguage(Locale.ENGLISH);
        }
        if (!PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext())
                .getBoolean("installed", false)) {
            PreferenceManager.getDefaultSharedPreferences(
                    getApplicationContext())
                    .edit().putBoolean("installed", true).apply();
            copyAssests();
            Log.v(TAG, "Successful");

        }
    }*/
/*
    @Override*/
    /*
        input-  gesture id indicating the type of gesture on screen
        description - Perform various actions based on the type of the gesture
     */
    protected boolean onGesture(int gestureId) {
        Log.v(TAG, String.format("Gesture id - %d\n", gestureId));
        switch(gestureId){
            case AccessibilityService.GESTURE_SWIPE_UP :
                tts.stop();
                reading_type = (reading_type+1)%3;
                switch (reading_type){
                    case 0:
                        tts.speak("default", TextToSpeech.QUEUE_FLUSH, null,null);
                        break;
                    case 1:
                        tts.speak("lines", TextToSpeech.QUEUE_FLUSH, null,null);
                        break;
                    case 2:
                        tts.speak("paras", TextToSpeech.QUEUE_FLUSH, null,null);
                        break;
                }
                break;
            case AccessibilityService.GESTURE_SWIPE_LEFT :
                switch(reading_type){
                    case 0:
                        tts.stop();
                        break;
                    //go to previous line
                    case 1:
                        if(curr_lang == ENG){
                            if(curr_line == 0)  curr_line = nLines-1;
                            else curr_line--;
                            tts.speak(lines[(curr_line)%nLines],TextToSpeech.QUEUE_FLUSH, null,null);
                            break;
                        }else{
                            if(tel_curr_line2 == 0)  tel_curr_line2 = nLines-1;
                            else tel_curr_line2--;
                            try {
                                synthesisWavInBackground2(tel_curr_line2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    //goto previous para
                    case 2:
                        if(curr_para == 0)  curr_para = nParas-1;
                        else curr_para--;
                        tts.speak(paras[(curr_para)%nParas],TextToSpeech.QUEUE_FLUSH, null,null);
                        break;
                }
                break;
            case AccessibilityService.GESTURE_SWIPE_RIGHT :
                switch(reading_type){
                    case 0:
                        //tts.stop();
                        stopAnyTTS();
                        break;
                    case 1:
                        //tts.speak(lines[(curr_line++)%nLines],TextToSpeech.QUEUE_FLUSH, null);
                        if(curr_lang == ENG)
                        speakAnyLanguage(lines[(curr_line++)%nLines]);
                        else
                            try {
                                synthesisWavInBackground2((tel_curr_line2++)%nLines);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        break;
                    case 2:
                        //tts.speak(paras[(curr_para++)%nParas],TextToSpeech.QUEUE_FLUSH, null);
                        speakAnyLanguage(paras[(curr_para++)%nParas]);
                        break;
                }
                break;
            case AccessibilityService.GESTURE_SWIPE_DOWN :
                stopAnyTTS();
                break;
            // to pop up settings menu
            case AccessibilityService.GESTURE_SWIPE_DOWN_AND_LEFT:

//                Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
                MyScreenReaderService.this.stopSelf();


                break;
            // to change language
            case AccessibilityService.GESTURE_SWIPE_DOWN_AND_RIGHT:
                if(curr_lang == ENG)    curr_lang = TEL;
                else if(curr_lang == TEL) curr_lang = ENG;
                Log.v(TAG,"!!!!CURR_LANG!!!!"+ " " + curr_lang);

                break;
            case AccessibilityService.GESTURE_SWIPE_UP_AND_RIGHT:
                //speak_telugu(getResources().getString(R.string.tel));
                speak_telugu("తెలుగు"); // debug text
                break;

        }
        return super.onGesture(gestureId);
    }

    /**
     * utilities for myAccessibilityService
     */
    private String getEventType(AccessibilityEvent event){
        Log.v(TAG,"I am in accessibility service" +" "+ event.getEventType());
      return AccessibilityEvent.eventTypeToString(event.getEventType());
    }

    /**
     * @param event
     * @return string description of event
     */
    private String getEventText(AccessibilityEvent event){
        StringBuilder sb = new StringBuilder();
        for(CharSequence a: event.getText()){
            sb.append(a);
		//Log.v(TAG,"sb" + sb);
        }
        //Log.v(TAG,"StringBuilder" +" "+ sb);
        return sb.toString();
    }
    /*
        Split text into lines
     */
    private void split_lines(String text) {
        // split the text into lines
        lines = text.split("[.!?]");
        Log.v(TAG,"Lines" +" "+ lines);
        nLines = lines.length;
        Log.v(TAG,"nLines" +" "+ nLines);

    }
    /*
       Split text into paras
    */
    private void split_paras(String text) {
        // split the text into paras
        paras = text.split("[\n]");
        Log.v(TAG,"Paras" +" "+ paras);
        nParas = paras.length;
    }
    /*
        Preprocess telugu text before passing to TTS
     */
    private String preProcessTelugu(String text){
	//Log.v(TAG "TEXT"+ + text);
        String inputtext = text.trim();
        //Log.v(TAG,"InputText"+ " " + inputtext);
        //String inputtext = res.replaceAll("\\s", ",");
        inputtext = inputtext.replaceAll("\\s", ",");
        inputtext = inputtext.replaceAll("\\,,",",");
        inputtext = inputtext.replaceAll("\\.,",".\n");
        //inputtext = inputtext.replaceAll("\\.,",".");
        inputtext = inputtext.replaceAll("\\?,",".\n");
        inputtext = inputtext.replaceAll("\\!,",".\n");
        inputtext = inputtext.replaceAll("\\... .,",".\n");
        //String[] split = inputtext.split(".");
        char[] ch = inputtext.toCharArray();
        int comma_limit = 8;
        int comma_count = 0;
        for(int i=0;i<ch.length;i++)
//System.out.print(ch[i]);
            if (ch[i] == ',') {
               // Log.v(TAG,"***ch"+" "+ch[i]);
//System.out.print(ch[i]);
                comma_count = comma_count + 1;
//System.out.print(comma_count);
                //Log.v(TAG,"comma_count"+" "+comma_count);

                if (comma_count % comma_limit == 0)
                {
                    ch[i] = '.';

                    //String s = inputtext.toString();
                    final String[] strs = inputtext.split("\\n");

                    //final String s = editText.getText().toString();
                    //Toast.makeText(MyScreenReaderService.this,s,Toast.LENGTH_SHORT).show();

                    final Spannable spannable = new SpannableString(inputtext);
                    spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, '\n', Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Log.v(TAG,"String is "+" "+inputtext);
                   // final Spannable spannable = new SpannableString(s);
                    //spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, '.', Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //Log.v(TAG,"@@@ch"+" "+ch);
                    //inputtext ="<font color=#FFFF00></font>";

                    System.out.println(ch);
/*String new_string = String.join('.',ch);
System.out.println(new_string);*/
                }

               /* if(ch[i] == '.'){
                    inputtext ="<font color=#FFFF00></font>";
                }*/
            }
        if(inputtext.endsWith( " ." )){
            inputtext = inputtext.substring(0, inputtext.length() - 2);
        }else if(inputtext.endsWith( " . " )){
            inputtext = inputtext.substring(0, inputtext.length() - 3);
        }
        inputtext = String.valueOf(ch);
        Log.v(TAG,"@@@inputtext"+" "+inputtext);
        return inputtext;

    }
    /*
        Preprocess before TTS for english text
     */
    private String preProcess(String text) {

		Log.v(TAG, "TEXT"+ " " +text);
        String ans;
        StringBuilder sb = new StringBuilder();
        //1. preprocess to create proper paras
        for (int i=0;i<text.length()-1;i++){
            char curr,next;
            curr = text.charAt(i);
            next = text.charAt(i+1);
            sb.append(curr);

            if(curr=='.'||curr=='!'||curr=='?'){
                if(next != ' '){
                    sb.append("\n");
                }
            }

            if(i==text.length()-2)  sb.append(next);
        }

        ans = sb.toString();
        //2. remove the front label if present
        String label = "Page Content: ";
        if(ans.length()>label.length()) {
            boolean flag = label.equals(ans.substring(0, label.length()));
            if (flag) ans = ans.substring(label.length());
        }

        return ans;
    }
    /*
        Stop English or Telugu TTS service
     */
    void stopAnyTTS(){
        stopSpeechFlag = true;
        if(curr_lang==ENG)  tts.stop();
        else stop_telugu_tts();
    }
    void speakAnyLanguage(String eventText){
        if(curr_lang == ENG)    tts.speak(eventText, TextToSpeech.QUEUE_FLUSH, null,null);
        else  speak_telugu(eventText);
    }
    /*
        Telugu Text to Speech for String msg
     */
    private void speak_telugu(String msg)  {
        Log.v(TAG,"@@@message@@@"+" "+msg);
        mp_free = false;
        //Log.i(TAG,"mp_free= false");
        if (msg != null) {
            displayText(msg);
            try {
                Log.v(TAG,"!!!message!!!"+" "+msg);

                synthesisWavInBackground(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private void stop_telugu_tts() {
        mediaPlayer.reset();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    /*
        Generating the Waveform and playing using media player for Telugu text
     */
    private void synthesisWavInBackground(String msg) throws IOException {
        Log.v(TAG,"####message####"+" "+msg);
    //setData(new String(msg));
   // Log.v(TAG,"$$$$msg"+ " "+ msg);
        setData(lines[tel_curr_line]);
        Log.v(TAG,"lines(tel_curr_line)"+ " " + (lines[tel_curr_line]));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(Float.parseFloat("0.05")));
                mp.reset();
                mp_free = true;
                Log.i(TAG, "mp_free= true1");
                if(!stopSpeechFlag)
                {
                    tel_curr_line++;
                    if (tel_curr_line < nLines) {
                        setData(lines[tel_curr_line]);

                        mediaPlayer.start();
                    } else {
                        tel_curr_line = 0;

                    }
                }
                else
                    tel_curr_line = 0;

            }
        });
        mediaPlayer.start();
        //inputtext.setTextColor(Color.parseColor("#ff0000"));
       // final Spannable spannable = new SpannableString(inputtext);
        //spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, '.', Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Log.i(TAG, "mp_free= true2");
    }
    private void synthesisWavInBackground2(int line_no) throws IOException {
        setData(lines[line_no]);
        //tel_curr_line2 = line_no;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
            }
        });
        mediaPlayer.start();
    }
    /*
        Set data source for media player after making C call to the flite-hts
     */
    public void setData(String msg){
        Log.v(TAG,"*******message********"+ " "+ msg);
        String inputtext = msg.trim(); //have to comment
	//String inputtext ="మసీదులోకి ఇద్దరు";
	// change here - String inputtext = hard coded text
        inputtext=inputtext.replace("|",".");
        inputtext=inputtext.replace(" . ", " .");
        inputtext=inputtext.replaceAll("\\s+", " ");
        inputtext=inputtext.trim();
        Log.v(TAG,"*******inputtext********"+ " "+ inputtext);
        if(inputtext.endsWith( " ." )){
            inputtext = inputtext.substring(0, inputtext.length() - 2);
        }else if(inputtext.endsWith( " . " )){
            inputtext = inputtext.substring(0, inputtext.length() - 3);
        }
        inputtext = inputtext.trim();
        Log.i(TAG,String.format("in setData-line = %s",inputtext));

        String speaker_name = "iitm_telugu";
        //                File outFile = new File(getExternalFilesDir(null), filename);
        File foldername = new File(getExternalFilesDir(null),speaker_name);
        Log.v(TAG,"FOLDERNAME"+ " " +foldername);
       // String foldername = Environment.getExternalStorageDirectory().getPath()+"/Android/data/"+getPackageName().toString()+"/";
       // String foldername = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.v(TAG,"FolderName"+ " "+ foldername);
        String filename = foldername+".htsvoice";
        Log.v(TAG,"FileName"+ " "+ filename);

        String wavname = foldername+"1.wav";
        Toast.makeText(getApplicationContext(),inputtext, Toast.LENGTH_SHORT).show();
        //



        Toast myToast =Toast.makeText(getApplicationContext(),mainfn(inputtext,filename,wavname),Toast.LENGTH_SHORT);
        myToast.show();
        //String displayedText = ((TextView)((LinearLayout)myToast.getView()).getChildAt(0)).getText().toString();
        // String displayedText = ((TextView)((RelativeLayout)myToast.getView()).getChildAt(0)).getText().toString();
        //Log.v(TAG,"I Am"+" "+displayedText+"DisplayedText");
        //final Spannable spannable = new SpannableString(inputtext);
        //spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, '.', Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        /////////////////////////////////////////////////////////////////////////////////
        //mediaPlayer = new MediaPlayer();

        Log.i("test", "media player reached hurray! ");
        //setAudioAttributes(AudioAttributes);
        //mediaPlayer.setAudioAttributes();
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(wavname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    public native String mainfn(String s, String inputtext, String wavname);
    static
    {
        System.loadLibrary("mainfn");
    }
    /*
     * utilities for flite-HTS
     */
    public void copyAssests()
    {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("tag", "Failed to get asset file list.", e);
        }
       // if (files != null)
        for (String filename : files)
        {
            System.out.println("In CopyAssets"+filename);
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                Log.v(TAG,"outFile"+ " "+outFile);
                out = new FileOutputStream(outFile);
                Log.v(TAG, "out"+" "+out);
                copyFile(in, out);
             /*   String foldername= Environment.getExternalStorageDirectory().getPath()+"/Android/data/"+getPackageName().toString()+"/";
                File folder = new File(foldername);
                folder.mkdirs();
                Log.v(TAG, "folder.mkdirs()" + " " + folder.mkdirs());
                File outfile = new File(foldername+filename);
                out = new FileOutputStream(outfile);
                Log.v(TAG, "out" + " " + out);

                copyFile(in, out);
                System.out.println("In copyAssets Entire Path"+foldername+filename);*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    private void displayText(String msg) {
//        TextView inputTextView = (TextView) findViewById(
//                R.id.inputText);
//        inputTextView.setText("" + msg);
        // create toast
        Log.v("test",String.format("dude- %s\n",msg) );
    }
    @Override

    public void onInit(int status) {

    }

}
