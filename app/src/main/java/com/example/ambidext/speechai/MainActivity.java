package com.example.ambidext.speechai;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    /* 변수 */
    private TextView txtSpeechInput, tvAnalysis;
    private Button btnSpeak, btnTTS, btnAnalysis;
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String name = null;
    String amount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 컴포넌트 설정 */
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        tvAnalysis = (TextView) findViewById(R.id.tvAnalysis);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);
        btnTTS = (Button) findViewById(R.id.btnTTS);
        btnAnalysis = (Button) findViewById(R.id.btnAnalysis);
        tts = new TextToSpeech(this, this);

        /* SST 버튼 클릭시*/
        btnSpeak.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                promptSpeechInput();
            }
        });

        /* TTS 버튼 클릭시*/
        btnTTS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                speakOut(tvAnalysis);
            }

        });

        /* btnAnalysis 버튼 클릭시 */
        btnAnalysis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setKeyWord(tvAnalysis);
            }
        });

        File path = getApplicationContext().getFilesDir();
        tvAnalysis.setText(path.toString());

        /*Komoran komoran = new Komoran("C:\\Users\\Jojangho\\Downloads\\SpeechAI\\models-full");

        List<List<Pair<String,String>>> result = komoran.analyze("hello", 2);

        for (List<Pair<String, String>> eojeolResult : result)
        {
            for (Pair<String, String> wordMorph : eojeolResult)
            {
                System.out.println(wordMorph);
            }
            System.out.println();
        }*/
    }

    /*  */
    public void setKeyWord(TextView tvAnalysis)
    {
        String input = txtSpeechInput.getText().toString();

        name = input.substring(0, spaceCheck(input));
        amount = input.substring(spaceCheck(input), spaceCheck(input) + 2);

        tvAnalysis.setText(name + " " + amount + "개가 맞습니까?");

        speakOut(tvAnalysis);

        promptSpeechInput();
    }

    public int spaceCheck(String spaceCheck)
    {
        for(int i = 0 ; i < spaceCheck.length() ; i++)
        {
            if(spaceCheck.charAt(i) == ' ')
                return i;
        }
        return 0;
    }

    public void corpusCheck()
    {
        ArrayList<CJSONParser.CData> corpusList = CJSONParser.GetInstance().getM_list();

        for(int i = 0 ; i < corpusList.size() ; i++)
        {
            CJSONParser.CData corpusObj = corpusList.get(i);

            if(name.equals(corpusObj.m_name))
            {
                if(Integer.parseInt(amount.trim()) < Integer.parseInt(corpusObj.m_amount))
                {
                    tvAnalysis.setText(name + " " + amount + "개 의 가격은 " + (Integer.parseInt(corpusObj.m_price) * Integer.parseInt(amount.trim())) + "원 입니다.");
                    speakOut(tvAnalysis);
                }
            }
        }
    }

    /* TTS 초기화 */
    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.KOREA);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TTS", "This Language is not supported");
            }
            else
            {
                btnSpeak.setEnabled(true);
                speakOut(tvAnalysis);
            }
        }
        else
        {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    /* TTS 실행 */
    private void speakOut(TextView tvAnalysis)
    {
        String text = tvAnalysis.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /* 음성인식 실행 */
    private void promptSpeechInput()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }


    /* 음성인식 결과 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT:
            {
                if (resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if(result.get(0).equals("네"))
                    {
                        corpusCheck();
                        break;
                    }
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy()
    {
        // Don't forget to shutdown tts!
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
