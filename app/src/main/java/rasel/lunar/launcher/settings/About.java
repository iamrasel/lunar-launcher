package rasel.lunar.launcher.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import rasel.lunar.launcher.databinding.AboutBinding;


public class About extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AboutBinding binding = AboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
