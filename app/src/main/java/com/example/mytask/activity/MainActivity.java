package com.example.mytask.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.mytask.R;
import com.example.mytask.presenter.CountPresenter;

public class MainActivity extends AppCompatActivity {
    CountPresenter presenter;
    EditText expression;
    Button count;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new CountPresenter();
        builder = new AlertDialog.Builder(MainActivity.this);
        expression = findViewById(R.id.expression);
        count = findViewById(R.id.count);

        count.setOnClickListener(v -> {
            int errorCode = presenter.clickListner(String.valueOf(expression.getText()), builder);
            if (errorCode != 0) {
                expression.setError(convertError(errorCode));
            }
        });
    }

    private String convertError(int code) {
        switch (code) {
            case 0:
                return "Complete";
            case 1:
                return "Используются запрещенные символы";
            case 2:
                return "Вы не ввели выражение";
            case 3:
                return "Выражение содержит ошибку";
            case 4:
                return "Вы пытаетесь разделить на 0...";
            default:
                return "Неизвестная ошибка";
        }
    }
}
