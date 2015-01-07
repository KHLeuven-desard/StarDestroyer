package com.satersoft.stardestroyer.domain;

import android.graphics.Bitmap;

import com.satersoft.stardestroyer.ui.SerialBitmap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InfoWrapper implements Serializable {
    public String file = new String();
    public Map<Integer, SerialBitmap> bitHash = new HashMap<Integer, SerialBitmap>();
    public Integer score = -1;
}
