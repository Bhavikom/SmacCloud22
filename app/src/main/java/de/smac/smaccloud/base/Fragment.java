package de.smac.smaccloud.base;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import de.smac.smaccloud.R;

public class Fragment extends android.support.v4.app.Fragment
{
    protected Activity activity;
    protected Context context;
    protected View rootView;
    protected Toolbar toolbar;
    protected ActionBar actionBar;
    protected FragmentManager fragmentManager;
    private NetworkService.RequestCompleteCallback networkCallback;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = (Activity) getActivity();
        context = getActivity();
        fragmentManager = getFragmentManager();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;
        setupToolBar();
        initializeComponents();
        bindEvents();
        refreshLayoutTypeface();
    }

    protected void initializeComponents()
    {

    }

    protected final void setupToolBar()
    {
        toolbar = (Toolbar) findViewById(R.id.navigationBar);
        if (toolbar != null)
        {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (activity != null)
            {
                activity.getSupportActionBar().setHomeButtonEnabled(true);
                activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected void bindEvents()
    {
        networkCallback = new NetworkService.RequestCompleteCallback()
        {
            @Override
            public void onRequestComplete(int requestCode, boolean status, String payload)
            {
                onNetworkResponse(requestCode, status, payload);
            }
        };
    }

    protected final void refreshLayoutTypeface()
    {
        Helper.setupTypeface(rootView, activity.robotoLightTypeface);
    }

    public final void notifySimple(String message)
    {
        Snackbar.make(activity.parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

    protected void postNetworkRequest(int requestCode, String url, String action, RequestParameter... requestParameters)
    {
        if (activity.networkBinder != null)
        {
            if (networkCallback == null)
            {
                networkCallback = new NetworkService.RequestCompleteCallback()
                {
                    @Override
                    public void onRequestComplete(int requestCode, boolean status, String payload)
                    {
                        onNetworkResponse(requestCode, status, payload);
                    }
                };
            }
            activity.networkBinder.postWrappedJSONRequest(activity, requestCode, url, action, networkCallback, requestParameters);
        }
    }

    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
    }

    protected View findViewById(int id)
    {
        return rootView.findViewById(id);
    }

    protected void navigateToFragment(int containerId, Fragment fragment)
    {
        navigateToFragment(containerId, fragment, false);
    }

    protected void navigateToFragment(int containerId, Fragment fragment, boolean addToBackStack)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack)
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

}