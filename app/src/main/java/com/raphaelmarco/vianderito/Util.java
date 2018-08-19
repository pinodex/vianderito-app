package com.raphaelmarco.vianderito;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;

public class Util {

    public static void applySpan(SpannableString spannable, String target, ClickableSpan span) {
        String spannableString = spannable.toString();

        int start = spannableString.indexOf(target);
        int end = start + target.length();

        spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
