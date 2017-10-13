package com.example.ambidext.speechai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jojangho on 2017-10-11.
 */

public class CJSONParser
{
    /**/
    private JSONData strJSON = null;
    private CData cData = null;

    public ArrayList<CData> m_list = null;

    public static CJSONParser instance = null;

    /**/
    public class CData
    {
        public String m_name;
        public String m_amount;
        public String m_price;
    }

    /**/
    public static void DestroyInstance()
    {
        instance = null;
    }

    public static CJSONParser GetInstance()
    {
        if(instance != null)
        {
            return instance;
        }
        instance = new CJSONParser();

        return instance;
    }

    public CJSONParser()
    {
        m_list = new ArrayList<CData>();

        Init();
    }

    /**/
    public void Init()
    {
        /**/
        strJSON = new JSONData();
        JSONArray jsonArray = null;

        /**/
        try
        {
            jsonArray = new JSONArray(strJSON.json);

            /**/
            JSONObject jsonObject = null;
            int i = 0;
            int length = jsonArray.length();
            while (i < length)
            {
                jsonObject = jsonArray.getJSONObject(i);

                cData = new CData();

                cData.m_name = jsonObject.getString("name");
                cData.m_amount = jsonObject.getString("amount");
                cData.m_price = jsonObject.getString("price");

                m_list.add(cData);
                i++;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace ();
        }
    }

    public ArrayList<CData> getM_list()
    {
        return m_list;
    }

    public void setM_list(ArrayList<CData> m_list)
    {
        this.m_list = m_list;
    }
}
