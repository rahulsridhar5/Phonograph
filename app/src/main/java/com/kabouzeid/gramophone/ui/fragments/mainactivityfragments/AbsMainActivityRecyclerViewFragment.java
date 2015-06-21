package com.kabouzeid.gramophone.ui.fragments.mainactivityfragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.views.FastScroller;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public abstract class AbsMainActivityRecyclerViewFragment extends AbsMainActivityFragment implements OnOffsetChangedListener {

    public static final String TAG = AbsMainActivityRecyclerViewFragment.class.getSimpleName();

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @Optional
    @InjectView(android.R.id.empty)
    TextView empty;
    @Nullable
    @Optional
    @InjectView(R.id.fast_scroller)
    FastScroller fastScroller;

    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (fastScroller != null) {
            fastScroller.setRecyclerView(recyclerView);
            fastScroller.setPressedHandleColor(getMainActivity().getThemeColorPrimary());
            fastScroller.setOnHandleTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }

        getMainActivity().addOnAppBarOffsetChangedListener(this);

        setUpRecyclerView();

        showEmptyMessageIfEmpty();
    }

    private void setUpRecyclerView() {
        mAdapter = createAdapter();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showEmptyMessageIfEmpty();
            }
        });

        recyclerView.setLayoutManager(createLayoutManager());
        recyclerView.setAdapter(mAdapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (fastScroller != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fastScroller.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, getMainActivity().getTotalAppBarScrollingRange() + i);
            fastScroller.setLayoutParams(params);
        }
    }

    @Override
    public void enableViews() {
        super.enableViews();
        recyclerView.setEnabled(true);
    }

    @Override
    public void disableViews() {
        super.disableViews();
        recyclerView.setEnabled(false);
    }

    private void showEmptyMessageIfEmpty() {
        if (empty != null) {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter != null) {
                empty.setText(getEmptyMessage());
                empty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @StringRes
    protected int getEmptyMessage() {
        return R.string.empty;
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.fragment_main_activity_recycler_view;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected abstract RecyclerView.LayoutManager createLayoutManager();

    protected abstract RecyclerView.Adapter createAdapter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
