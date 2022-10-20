/*
 * Lunar Launcher
 * Copyright (C) 2022 Md Rasel Hossain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rasel.lunar.launcher.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import rasel.lunar.launcher.apps.AppDrawer
import rasel.lunar.launcher.feeds.Feeds
import rasel.lunar.launcher.home.LauncherHome


internal class ViewPagerAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle?) :
    FragmentStateAdapter(fragmentManager!!, lifecycle!!) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return Feeds()
            1 -> return LauncherHome()
            2 -> return AppDrawer()
        }
        return LauncherHome()
    }

    override fun getItemCount(): Int {
        return 3
    }

}
