package com.example.mytask.presenter;

import android.support.v7.app.AlertDialog;
import android.util.Pair;

import com.example.mytask.model.SimpleCounter;

import java.text.DecimalFormat;

@SuppressWarnings("WeakerAccess")
public class CountPresenter {
    public int clickListner(String expression, AlertDialog.Builder builder) {
        Pair<Integer, Double> result = SimpleCounter.getInstance().count(expression);
        if (result.first == 0) {
            DecimalFormat format = new DecimalFormat();
            format.setDecimalSeparatorAlwaysShown(false);
//            format.format(result.second);
            builder.setTitle("Result!")
                    .setMessage(format.format(result.second))
                    .setCancelable(false)
                    .setNegativeButton("Ok!",
                            (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
            return 0;
        } else {
            return result.first;
        }
    }
}
