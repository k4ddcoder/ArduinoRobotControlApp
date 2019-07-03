package com.example.robotcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jackandphantom.joystickview.JoyStickView;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int maxSpeed = 200;
    private int minSpeed = -200;
    private int[] velocitiesRL;
    private TextView x;
    private TextView y;
    private TextView a;
    private  TextView s;
    private int[] anterior = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        a = findViewById(R.id.a);
        s = findViewById(R.id.s);
        anterior[0] = 0;
        anterior[1] = 0;

        final JoyStickView joyStickView = findViewById(R.id.joystick);
        joyStickView.setOnMoveListener(new JoyStickView.OnMoveListener() {
            @Override
            public void onMove(double angle, float strength) {

            velocitiesRL = calculateServoSpeed(angle, strength);

            if(checkLast()) {

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://192.168.4.55/robotctrl?velL=" + velocitiesRL[0] + "&velR=" + velocitiesRL[1];

                StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                );
                queue.add(postRequest);
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                x.setText("X: " + df.format(Math.abs(strength)*2.5*Math.cos(angle)));
                y.setText("Y: " + df.format(Math.abs(strength)*2.5*Math.sin(angle)));
                a.setText("A:" + angle + "º");
                s.setText("S:" + strength);


            }


            }
        });

        Button button = findViewById(R.id.stop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://192.168.4.55/robotctrl?velL=0&velR=0";

                StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                );
                queue.add(postRequest);
                x.setText("X: 0");
                y.setText("Y: 0");

            }
        });
    }

    private boolean checkLast() {

        boolean check = false;

        if((Math.abs(velocitiesRL[0] - anterior[0]) > 10) || (Math.abs(velocitiesRL[1] - anterior[1]) > 10)) check = true;

        anterior[0] = velocitiesRL[0];
        anterior[1] = velocitiesRL[1];

        return check;

    }


    private int[] calculateServoSpeed(double angle, float strength) {
        int[] servoVelocities = new int[2];
        boolean up = checkUp(angle);

        strength = (float) (strength * 2.5);

        int velocidadSin = (int) Math.abs(strength*Math.sin(Math.abs(2*angle/3)+(Math.PI/6)));
        float anguloCos = (float) Math.cos(angle);

        if(anguloCos > 0){
            servoVelocities[0] = (int) strength;
            servoVelocities[1] = velocidadSin;
        }else {
            servoVelocities[1] = (int) strength;
            servoVelocities[0] = velocidadSin;
        }

        if(!up) {
            servoVelocities[0] *= -1;
            servoVelocities[1] *= -1;
        }

        return servoVelocities;
    }

    private boolean checkRight(double angle) {
        return (angle <= 90 && angle >= 270) ? true : false;
    }

    private boolean checkUp(double angle) {
        return (angle >= 0 && angle <= 180) ? true : false;
    }


}
