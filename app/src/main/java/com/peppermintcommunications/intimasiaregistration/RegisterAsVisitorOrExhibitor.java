package com.peppermintcommunications.intimasiaregistration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterAsVisitorOrExhibitor extends AppCompatActivity {

    Button visitor, exhibitor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_visitor_or_exhibitor);

        visitor = (Button) findViewById(R.id.register_visitor);
        exhibitor = (Button) findViewById(R.id.register_exhibitor);

        visitor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RegisterAsVisitorOrExhibitor.this, FirstStepToRegisteration.class);
                        startActivity(intent);
                    }
                }
        );

        exhibitor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RegisterAsVisitorOrExhibitor.this, ExhibitorRegistration.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
