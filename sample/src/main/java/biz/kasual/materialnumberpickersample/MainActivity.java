package biz.kasual.materialnumberpickersample;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mDefaultButton;
    private Button mSimpleButton;
    private Button mCustomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDefaultButton = (Button)findViewById(R.id.default_number_picker_button);
        mSimpleButton = (Button)findViewById(R.id.simple_number_picker_button);
        mCustomButton = (Button)findViewById(R.id.custom_number_picker_button);

        mDefaultButton.setOnClickListener(this);
        mSimpleButton.setOnClickListener(this);
        mCustomButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NumberPicker picker = null;


        if (v.equals(mDefaultButton)) {
            picker = new NumberPicker(this);
            picker.setMinValue(1);
            picker.setMaxValue(10);
        }
        else {
            MaterialNumberPicker.Builder numberPickerBuilder = new MaterialNumberPicker.Builder(this);

            if (v.equals(mCustomButton)) {
                numberPickerBuilder
                        .minValue(1)
                        .maxValue(50)
                        .defaultValue(10)
                        .separatorColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .textColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .formatter(new NumberPicker.Formatter() {
                            @Override
                            public String format(int value) {
                                return "Formatted text for value " + value;
                            }
                        });
            }

            picker = numberPickerBuilder.build();
        }

        new AlertDialog.Builder(this).setView(picker).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
