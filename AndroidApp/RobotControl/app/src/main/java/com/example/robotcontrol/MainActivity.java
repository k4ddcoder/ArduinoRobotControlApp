package com.example.robotcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jackandphantom.joystickview.JoyStickView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private int[] velocitiesRL = new int[2];
    private int[] anterior = new int[2];
    private int strength_progres = 0;
    private SeekBar seekbar;
    private final OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anterior[0] = 0;
        anterior[1] = 0;

        seekbar = findViewById(R.id.strength);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                strength_progres = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final JoyStickView joyStickView = findViewById(R.id.joystick);
        joyStickView.setOnMoveListener(new JoyStickView.OnMoveListener() {
            @Override
            public void onMove(double angle, float strength) {

            velocitiesRL = calculateServoSpeed(angle, strength_progres);
            String url = "http://192.168.4.55/robotctrl?velL=" + velocitiesRL[0] + "&velR=" + velocitiesRL[1];

            if(checkLast()) {
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .build();
                okhttp3.Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    public void onResponse(Call call, okhttp3.Response response)
                            throws IOException {
                        // ...
                    }

                });

            }

        }

        });

        Button button = findViewById(R.id.stop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "http://192.168.4.55/robotctrl?velL=0&velR=0";

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .build();
                okhttp3.Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    public void onResponse(Call call, okhttp3.Response response)
                            throws IOException {
                        // ...
                    }

                });

            }
        });
    }

    private boolean checkLast() {

        boolean check = false;

        if((Math.abs(velocitiesRL[0] - anterior[0]) > 20) || (Math.abs(velocitiesRL[1] - anterior[1]) > 20)) check = true;

        anterior[0] = velocitiesRL[0];
        anterior[1] = velocitiesRL[1];

        return check;

    }


    private int[] calculateServoSpeed(double angle, float strength) {
        int[] servoVelocities = new int[2];
        double x = strength_progres * Math.cos(Math.toRadians(angle));
        double y = strength_progres * Math.sin(Math.toRadians(angle));
        double relative_angle = angle % 90;
        System.out.println("RELATIVE: " + relative_angle);
        double tcoeff = (relative_angle / 90);
        System.out.println("ANGLE: " + (int) Math.round(angle));
        System.out.println("TCOEFF: " + tcoeff);

        if(x >= 0 && y >= 0) { //1st cuad
            System.out.println("PRIMER");
            servoVelocities[0] = strength_progres;
            servoVelocities[1] = (int) Math.round(strength_progres * tcoeff);


        }else if(x < 0 && y >= 0) { //2nd cuad
            System.out.println("SEGON");
            servoVelocities[1] = strength_progres;
            servoVelocities[0] = (int) Math.round(strength_progres * (1-tcoeff));


        }else if(x < 0 && y < 0) { //3rd cuad
            System.out.println("TERCER");
            servoVelocities[1] = strength_progres * -1;
            servoVelocities[0] = (int) Math.round(strength_progres * tcoeff) * -1;

        }else { //4rd cuad
            System.out.println("QUART");
            servoVelocities[0] = strength_progres * -1;
            servoVelocities[1] = (int) Math.round(strength_progres * (1 -tcoeff)) * -1;

        }
        System.out.println("L: " + servoVelocities[0] + " RIGHT: " + servoVelocities[1]);

        return servoVelocities;
    }


    private boolean checkRight(double angle) {
        return (angle <= 90 && angle >= 270) ? true : false;
    }

    private boolean checkUp(double angle) {
        return (angle >= 0 && angle <= 180) ? true : false;
    }


}
