package rasel.lunar.launcher.helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import rasel.lunar.launcher.apps.AppDrawer;
import rasel.lunar.launcher.feeds.Feeds;
import rasel.lunar.launcher.home.LauncherHome;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Feeds();
            case 1:
                return new LauncherHome();
            case 2:
                return new AppDrawer();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
