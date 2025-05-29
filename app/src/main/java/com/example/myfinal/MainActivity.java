package com.example.myfinal;

import android.app.AlarmManager;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.PendingIntent;


public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCities;
    private TextView tvWeather;
    private ProgressBar progressBar;
    private Button btnWater;

    private final String API_KEY = "3c045a4f9fa243a3a8509f0cbd141660";
    private final String API_HOST = "https://pu3qqptrdm.re.qweatherapi.com";

    private String currentTemp = null;
    private String currentHumidity = null;

    // 省会城市及其 locationId（你可以添加更多城市）
    private final Map<String, String> cityMap = new HashMap<String, String>() {{
        put("北京", "101010100");
        put("天津", "101030100");
        put("上海", "101020100");
        put("重庆", "101040100");
        put("哈尔滨", "101050101");
        put("长春", "101060101");
        put("沈阳", "101070101");
        put("呼和浩特", "101080101");
        put("石家庄", "101090101");
        put("乌鲁木齐", "101130101");
        put("兰州", "101160101");
        put("西宁", "101150101");
        put("银川", "101170101");
        put("郑州", "101180101");
        put("济南", "101120101");
        put("太原", "101100101");
        put("合肥", "101220101");
        put("武汉", "101200101");
        put("南京", "101190101");
        put("杭州", "101210101");
        put("南昌", "101240101");
        put("广州", "101280101");
        put("福州", "101230101");
        put("南宁", "101300101");
        put("成都", "101270101");
        put("贵阳", "101260101");
        put("昆明", "101290101");
        put("拉萨", "101140101");
        put("西安", "101110101");
        put("长沙", "101250101");
        put("海口", "101310101");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCities = findViewById(R.id.spinnerCities);
        tvWeather = findViewById(R.id.tvWeather);
        progressBar = findViewById(R.id.progressBar);
        btnWater = findViewById(R.id.btnWater);
        btnWater.setEnabled(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cityMap.keySet().toArray(new String[0]));
        spinnerCities.setAdapter(adapter);

        spinnerCities.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String city = parent.getItemAtPosition(position).toString();
                String locationId = cityMap.get(city);
                fetchWeather(locationId);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                //nothing
            }
        });

        btnWater.setOnClickListener(v -> {
            if (currentTemp != null && currentHumidity != null) {
                Intent intent = new Intent(MainActivity.this, WaterActivity.class);
                intent.putExtra("temp", currentTemp);
                intent.putExtra("humidity", currentHumidity);
                startActivity(intent);
            }
        });

        Button btnWaterRecord = findViewById(R.id.btnWaterRecord);
        btnWaterRecord.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WaterRecordActivity.class);
            startActivity(intent);
        });

    }



    private void fetchWeather(String locationId) {
        progressBar.setVisibility(View.VISIBLE);
        tvWeather.setText("加载中...");
        btnWater.setEnabled(false);

        String url = API_HOST + "/v7/weather/now?location=" + locationId + "&key=" + API_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvWeather.setText("啊哦？" + e.getMessage());
                    btnWater.setEnabled(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string();
                mainHandler.post(() -> {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        JSONObject now = obj.getJSONObject("now");

                        currentTemp = now.getString("temp");
                        currentHumidity = now.getString("humidity");
                        String text = now.getString("text");

                        String display = "当前天气：" + text + "\n温度：" + currentTemp + "℃\n湿度：" + currentHumidity + "%";
                        tvWeather.setText(display);
                        progressBar.setVisibility(View.GONE);
                        btnWater.setEnabled(true);

                    } catch (Exception e) {
                        tvWeather.setText("好像出了点问题？" + e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        btnWater.setEnabled(false);
                    }
                });
            }


        });
    }

    private void scheduleDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, WaterReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 提醒时间每天晚上8点
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // 每天重复一次
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

}
