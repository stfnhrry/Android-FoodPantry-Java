package com.example.foodpantryjava;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public abstract class DemoActivity extends AppCompatActivity implements HasAndroidInjector {

  public static final String EXTRA_DEMO_TITLE = "demo_title";

  static final String EXTRA_TRANSITION_NAME = "EXTRA_TRANSITION_NAME";

  private Toolbar toolbar;
  private ViewGroup demoContainer;

  @Inject
  DispatchingAndroidInjector<Object> androidInjector;

//  @Override
//  protected void onCreate(@Nullable Bundle bundle) {
//    if (shouldSetUpContainerTransform()) {
//      String transitionName = getIntent().getStringExtra(EXTRA_TRANSITION_NAME);
//      findViewById(android.R.id.content).setTransitionName(transitionName);
//      setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
//      getWindow().setSharedElementEnterTransition(buildContainerTransform(/* entering= */ true));
//      getWindow().setSharedElementReturnTransition(buildContainerTransform(/* entering= */ false));
//    }
//
//    safeInject();
//    super.onCreate(bundle);
//    WindowPreferencesManager windowPreferencesManager = new WindowPreferencesManager(this);
//    windowPreferencesManager.applyEdgeToEdgePreference(getWindow());
//
//    setContentView(R.layout.cat_demo_activity);
//
//    toolbar = findViewById(R.id.toolbar);
//    demoContainer = findViewById(R.id.cat_demo_activity_container);
//
//    initDemoActionBar();
//    demoContainer.addView(onCreateDemoView(LayoutInflater.from(this), demoContainer, bundle));
//  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @StringRes
  public int getDemoTitleResId() {
    return 0;
  }

  protected boolean shouldShowDefaultDemoActionBar() {
    return true;
  }

  protected boolean shouldShowDefaultDemoActionBarCloseButton() {
    return true;
  }

  protected boolean shouldSetUpContainerTransform() {
    return VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP
            && getIntent().getStringExtra(EXTRA_TRANSITION_NAME) != null;
  }

  @Override
  public AndroidInjector<Object> androidInjector() {
    return androidInjector;
  }

  private void safeInject() {
    try {
      AndroidInjection.inject(this);
    } catch (Exception e) {
      // Ignore exception, not all DemoActivity subclasses need to inject
    }
  }

  @RequiresApi(VERSION_CODES.LOLLIPOP)
  private MaterialContainerTransform buildContainerTransform(boolean entering) {
    MaterialContainerTransform transform = new MaterialContainerTransform(this, entering);
    transform.addTarget(android.R.id.content);
    transform.setContainerColor(
            MaterialColors.getColor(findViewById(android.R.id.content), R.attr.colorSurface));
    transform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
    return transform;
  }

  private void initDemoActionBar() {
    if (shouldShowDefaultDemoActionBar()) {
      setSupportActionBar(toolbar);
      setDemoActionBarTitle(getSupportActionBar());

      if (shouldShowDefaultDemoActionBarCloseButton()) {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_delete_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }
    } else {
      toolbar.setVisibility(View.GONE);
    }
  }

  private void setDemoActionBarTitle(ActionBar actionBar) {
    if (getDemoTitleResId() != 0) {
      actionBar.setTitle(getDemoTitleResId());
    } else {
      actionBar.setTitle(getDefaultDemoTitle());
    }
  }

  private String getDefaultDemoTitle() {
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      return extras.getString(EXTRA_DEMO_TITLE, "");
    } else {
      return "";
    }
  }

  public abstract View onCreateDemoView(
          LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle);
}
