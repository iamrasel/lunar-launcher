package rasel.lunar.launcher.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.databinding.MainSettingsBinding;
import rasel.lunar.launcher.helpers.Constants;

public class SettingsActivity extends AppCompatActivity {

    private final Constants constants = new Constants();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainSettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.main_settings);
    }

    @SuppressLint("NonConstantResourceId")
    public void openSettingsFragments(View view) {
        switch(view.getId()) {
            case R.id.launcher_home_settings:
                (new LauncherHomeSettings()).show(getSupportFragmentManager(), constants.MODAL_BOTTOM_SHEET_TAG);
                break;
            case R.id.more_settings:
                (new MoreSettings()).show(getSupportFragmentManager(), constants.MODAL_BOTTOM_SHEET_TAG);
                break;
            case R.id.about:
                (new About()).show(getSupportFragmentManager(), constants.MODAL_BOTTOM_SHEET_TAG);
                break;
        }
    }
}
