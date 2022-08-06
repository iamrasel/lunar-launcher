package rasel.lunar.launcher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import rasel.lunar.launcher.databinding.MainActivityBinding;
import rasel.lunar.launcher.helpers.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    private ViewPager2 viewPager;
    private boolean executeOnResume;
    private FragmentRefreshListener fragmentRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        executeOnResume = false;
        setUpView();
    }

    private void setUpView() {
        final RecyclerView.Adapter<?> adapter;
        viewPager = binding.viewPager;
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() != 0) {
            if(getFragmentRefreshListener()!= null){
                getFragmentRefreshListener().onRefresh();
            }
            getSupportFragmentManager().popBackStack();
        }
        if (viewPager.getCurrentItem() == 0 | viewPager.getCurrentItem() == 2) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(executeOnResume) {
            this.recreate();
        } else {
            executeOnResume = true;
        }
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }
    public interface FragmentRefreshListener{
        void onRefresh();
    }
    private FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }
}
