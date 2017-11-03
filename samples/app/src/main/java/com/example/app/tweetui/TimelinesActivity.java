/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.app.tweetui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.app.R;

/**
 * TimelinesActivity pages between different timeline Fragments.
 */
public class TimelinesActivity extends AppCompatActivity {
    private static final int PAGE_SEARCH = 0;
    private static final int PAGE_USER = 1;
    private static final int PAGE_USER_RECYCLER_VIEW = 2;
    private static final int PAGE_COLLECTION = 3;
    private static final int PAGE_LIST = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        final FragmentManager fm = getSupportFragmentManager();
        final FragmentPagerAdapter pagerAdapter = new TimelinePagerAdapter(fm, getResources());
        final ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static class TimelinePagerAdapter extends FragmentPagerAdapter {
        // titles for timeline fragments, in order
        private static final int[] PAGE_TITLE_RES_IDS = {
                R.string.search_timeline_title,
                R.string.user_timeline_title,
                R.string.user_recycler_view_timeline_title,
                R.string.collection_timeline_title,
                R.string.list_timeline_title,
        };
        private Resources resources;

        public TimelinePagerAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            this.resources = resources;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PAGE_SEARCH:
                    return SearchTimelineFragment.newInstance();
                case PAGE_USER:
                    return UserTimelineFragment.newInstance();
                case PAGE_USER_RECYCLER_VIEW:
                    return UserTimelineRecyclerViewFragment.newInstance();
                case PAGE_COLLECTION:
                    return CollectionTimelineFragment.newInstance();
                case PAGE_LIST:
                    return ListTimelineFragment.newInstance();
                default:
                    throw new IllegalStateException("Unexpected Fragment page item requested.");
            }
        }

        @Override
        public int getCount() {
            return PAGE_TITLE_RES_IDS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PAGE_SEARCH:
                    return resources.getString(PAGE_TITLE_RES_IDS[PAGE_SEARCH]);
                case PAGE_USER:
                    return resources.getString(PAGE_TITLE_RES_IDS[PAGE_USER]);
                case PAGE_USER_RECYCLER_VIEW:
                    return resources.getString(PAGE_TITLE_RES_IDS[PAGE_USER_RECYCLER_VIEW]);
                case PAGE_COLLECTION:
                    return resources.getString(PAGE_TITLE_RES_IDS[PAGE_COLLECTION]);
                case PAGE_LIST:
                    return resources.getString(PAGE_TITLE_RES_IDS[PAGE_LIST]);
                default:
                    throw new IllegalStateException("Unexpected Fragment page title requested.");
            }
        }
    }
}
