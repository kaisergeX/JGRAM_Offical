package com.example.project;

import android.text.Layout;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

public class SetupSkeleton {

    public static void createSkeletion(RecyclerView recyclerView, int layout, RecyclerView.Adapter adapter) {
        final SkeletonScreen skeletonScreen = com.ethanhua.skeleton.Skeleton.bind(recyclerView)
                .adapter(adapter)
                .shimmer(true)
                .color(R.color.shimmer)
                .angle(20)
                .frozen(true)
                .duration(1200)
                .count(3)
                .load(layout)
                .show(); //default count is 10

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                skeletonScreen.hide();
            }
        }, 1200);
    }

    public static void createSkeletionForView(View view, int layout) {
        final SkeletonScreen skeletonScreen = Skeleton.bind(view)
                .load(layout)
                .shimmer(true)
                .color(R.color.shimmer)
                .angle(20)
                .duration(1000)
                .show();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                skeletonScreen.hide();
            }
        }, 1000);
    }

}
