package com.raphaelmarco.vianderito.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.squareup.picasso.Picasso;

public class PicassoImageView extends AppCompatImageView {

    public PicassoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageURL(String url){
        Picasso.get().load(url).into(this);
    }

}
