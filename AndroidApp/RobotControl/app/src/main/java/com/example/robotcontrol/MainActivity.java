package com.example.robotcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.jackandphantom.joystickview.JoyStickView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoyStickView joyStickView = findViewById(R.id.joystick);
        joyStickView.setOnMoveListener(new JoyStickView.OnMoveListener() {
            @Override
            public void onMove(double angle, float strength) {

                System.out.println("Angle: " + angle);
                System.out.println("Strenght" + strength);
            }
        });
    }


}
