package com.example.project;

import androidx.navigation.NavOptions;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class Transition {

    public static NavOptions getNavOptions() {

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build();

        return navOptions;
    }


}
