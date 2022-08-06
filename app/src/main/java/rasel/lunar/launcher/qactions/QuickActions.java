package rasel.lunar.launcher.qactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import rasel.lunar.launcher.databinding.QuickActionsBinding;

public class QuickActions extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        QuickActionsBinding binding = QuickActionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
