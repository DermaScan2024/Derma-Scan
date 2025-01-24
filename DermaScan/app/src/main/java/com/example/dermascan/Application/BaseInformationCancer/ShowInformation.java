package com.example.dermascan.Application.BaseInformationCancer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dermascan.R;

public class ShowInformation extends AppCompatActivity {
    private TextView title;
    private TextView informationCancer;
    private TextView information_Prevention;

    private final int MELANOMA_ID = 1;
    private final int ESPINO_ID = 2;
    private final int BASO_ID = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_information);
        title =(TextView) findViewById(R.id.title_InformationCancer);
        informationCancer =(TextView) findViewById(R.id.information);
        information_Prevention =(TextView) findViewById(R.id.information_Prevention);
        System.out.println(getIntent().getIntExtra("typeInformation",0));
        switch (getIntent().getIntExtra("typeInformation",0)){
            case MELANOMA_ID:
                title.setText(R.string.melanoma_title);
                informationCancer.setText(R.string.melanoma_information);
                information_Prevention.setText(R.string.melanoma_prevention);
                break;

            case ESPINO_ID:
                title.setText(R.string.espino_title);
                informationCancer.setText(R.string.espino_information);
                information_Prevention.setText(R.string.espino_prevention);
                break;

            case BASO_ID:
                title.setText(R.string.baso_title);
                informationCancer.setText(R.string.baso_information);
                information_Prevention.setText(R.string.baso_prevention);
                break;
        }
    }
}