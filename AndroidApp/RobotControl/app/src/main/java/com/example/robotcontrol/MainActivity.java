package com.example.robotcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int maxSpeed = 200;
    private int minSpeed = -200;
    private int[] velocitiesRL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoyStickView joyStickView = findViewById(R.id.joystick);
        joyStickView.setOnMoveListener(new JoyStickView.OnMoveListener() {
            @Override
            public void onMove(double angle, float strength) {

                velocitiesRL = calculateServoSpeed(angle, strength);

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


            }
        });
    }


    private int[] calculateServoSpeed(double angle, float strength) {
        int[] servoVelocities = new int[3];
        boolean up = checkUp(angle);

        //Right or Left direction
        if(checkRight(angle)) {
            servoVelocities[0] = Math.round(strength);
            servoVelocities[1] = (up) ? (int) Math.round(strength-((90 - angle)*1.5)) : (int) Math.round(strength-((90 - angle)*1.5));

        }else {
            servoVelocities[1] = Math.round(strength);
            servoVelocities[0] = (up) ? (int) Math.round(strength-((angle - 90)*1.5)) : (int) Math.round(strength-((angle - 90)*1.5));

        }

        return servoVelocities;
    }

    private boolean checkRight(double angle) {
        return (angle <= 90 && angle >= 270) ? true : false;
    }

    private boolean checkUp(double angle) {
        return (angle >= 0 && angle <= 270) ? true : false;
    }


}
