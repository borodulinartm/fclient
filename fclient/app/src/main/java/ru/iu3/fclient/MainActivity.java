package ru.iu3.fclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ru.iu3.fclient.databinding.ActivityMainBinding;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

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

        Button button = binding.sampleButton;
    }

    public void onButtonClick(View view) {
        // Выводим Toast на экран
        Toast.makeText(this, "Clicked!!!", Toast.LENGTH_SHORT).show();
    }

    public static byte[] stringToHex(String s) {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (DecoderException decoderException) {
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