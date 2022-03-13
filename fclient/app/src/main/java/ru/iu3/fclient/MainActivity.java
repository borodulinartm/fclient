package ru.iu3.fclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ru.iu3.fclient.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TransactionEvents {
    ActivityResultLauncher<Intent> activityResultLauncher;

    private String pin;

    // Used to load the 'RPO2022' library on application startup.
    static {
        System.loadLibrary("fclient");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int res = initRng();
        byte[] keys = randomBytes(16);

        Random random = new Random();

        byte[] data = new byte[20];
        for (int i = 0; i < data.length; ++i) {
            data[i] = (byte) ((byte) random.nextInt() % 255);
        }

        // Пример шифрованя данных (в отладчике)
        byte[] encrypt_data = encrypt(keys, data);

        // Пример дешифрования данных (в отладчике)
        byte[] decrypt_data = decrypt(keys, encrypt_data);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            pin = intent.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                }
        );
    }

//    public void onButtonClick(View view) {
//        //Toast.makeText(this, "Clicked!!!", Toast.LENGTH_SHORT).show();
//        // Ключ, по которому будем осуществлять шифрование данных
//        byte[] key = stringToHex("0123456789ABCDEF0123456789ABCDE0");
//
//        // Шифрование и дешифрование данных
//        byte[] encryptedData = encrypt(key, stringToHex("000000000000000102"));
//        byte[] decryptData = decrypt(key, encryptedData);
//
//        // На осноании байтового массива получаем строку как конвертирование HEX-а
//        String s = new String(Hex.encodeHex(decryptData)).toUpperCase();
//        // Выводим на экран Toast
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
//    }

    public void onButtonClick(View view) {
        // Добавляем к 3 ЛР метод транзакции данных
        new Thread(() -> {
           try {
               byte[] trd = stringToHex("9F0206000000000100");
               transaction(trd);
           } catch (Exception exception) {
               Log.println(Log.ERROR, "MtLog", Arrays.toString(exception.getStackTrace()));
           }
        }).start();
    }

    // Метод осуществляет конвертирование из String в HEX
    public static byte[] stringToHex(String s) {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (DecoderException decoderException) {
            // При возникновении ошибок выводим в LogCat сообщение об ошибке
            Log.println(Log.ERROR, "MtLog", decoderException.getMessage());
            hex = null;
        }

        return hex;
    }
    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
    public native boolean transaction(byte[] trd);

    // Переопределяем метод, который написан в интерфейсе
    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();

        Intent intent = new Intent(MainActivity.this, PinpadActivity.class);
        intent.putExtra("ptc", ptc);
        intent.putExtra("amount", amount);

        synchronized (MainActivity.this) {
            activityResultLauncher.launch(intent);
            try {
                MainActivity.this.wait();
            } catch (Exception exception) {
                Log.println(Log.ERROR, "MtLog", exception.getMessage());
            }
        }

        return pin;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }
}