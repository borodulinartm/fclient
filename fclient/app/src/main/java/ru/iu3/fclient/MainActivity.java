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

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;

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
                            String pin = intent.getStringExtra("pin");

                            Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, PinpadActivity.class);

        // Вот здесь произошла замена: вместо startActivity поставил launch
        activityResultLauncher.launch(intent);
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
}