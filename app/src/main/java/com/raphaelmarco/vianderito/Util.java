package com.raphaelmarco.vianderito;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;

import com.raphaelmarco.vianderito.activity.DocumentActivity;

public class Util {

    public static void applySpan(SpannableString spannable, String target, ClickableSpan span) {
        String spannableString = spannable.toString();

        int start = spannableString.indexOf(target);
        int end = start + target.length();

        spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void openDocument(Context context, String title) {
        Intent i = new Intent(context, DocumentActivity.class);
        i.putExtra("name", title);

        context.startActivity(i);
    }

}
