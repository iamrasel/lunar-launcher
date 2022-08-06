package rasel.lunar.launcher.feeds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import rasel.lunar.launcher.databinding.FeedsBinding;

public class Feeds extends Fragment {

    private FeedsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FeedsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
