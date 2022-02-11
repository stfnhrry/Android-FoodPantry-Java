package com.example.foodpantryjava;

import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

/** Utility class for the Adaptive package. */
class AdaptiveUtils {

  static final int MEDIUM_SCREEN_WIDTH_SIZE = 600;
  static final int LARGE_SCREEN_WIDTH_SIZE = 1240;

  private AdaptiveUtils() {}

  /**
   * Updates the visibility of the main navigation view components according to screen size.
   *
   * <p>The small screen layout should have a bottom navigation and optionally a fab. The medium
   * layout should have a navigation rail with a fab, and the large layout should have a navigation
   * drawer with an extended fab.
   */
  static void updateNavigationViewLayout(
          int screenWidth,
          @NonNull DrawerLayout drawerLayout,
          @NonNull NavigationView modalNavDrawer,
          @Nullable FloatingActionButton fab,
          @NonNull View bottomNav,
          @NonNull NavigationRailView navRail,
          @NonNull NavigationView navDrawer,
          @NonNull ExtendedFloatingActionButton navFab) {
    // Set navigation menu button to show a modal navigation drawer in medium screens.
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    setNavRailButtonOnClickListener(
            drawerLayout, navRail.getHeaderView().findViewById(R.id.nav_button), modalNavDrawer);
    setModalDrawerButtonOnClickListener(
            drawerLayout,
            modalNavDrawer.getHeaderView(0).findViewById(R.id.nav_button),
            modalNavDrawer);

    if (screenWidth < AdaptiveUtils.MEDIUM_SCREEN_WIDTH_SIZE) {
      // Small screen
      if (fab != null) {
        fab.setVisibility(View.VISIBLE);
      }
      bottomNav.setVisibility(View.VISIBLE);
      navRail.setVisibility(View.GONE);
      navDrawer.setVisibility(View.GONE);
    } else if (screenWidth < AdaptiveUtils.LARGE_SCREEN_WIDTH_SIZE) {
      // Medium screen
      if (fab != null) {
        fab.setVisibility(View.GONE);
      }
      bottomNav.setVisibility(View.GONE);
      navRail.setVisibility(View.VISIBLE);
      navDrawer.setVisibility(View.GONE);
      navFab.shrink();
    } else {
      // Large screen
      if (fab != null) {
        fab.setVisibility(View.GONE);
      }
      bottomNav.setVisibility(View.GONE);
      navRail.setVisibility(View.GONE);
      navDrawer.setVisibility(View.VISIBLE);
      navFab.extend();
    }
  }

  /* Sets navigation rail's header button to open the modal navigation drawer. */
  private static void setNavRailButtonOnClickListener(
          @NonNull DrawerLayout drawerLayout,
          @NonNull View navButton,
          @NonNull NavigationView modalDrawer) {
    navButton.setOnClickListener(
            new OnClickListener() {
              @Override
              public void onClick(View v) {
                drawerLayout.openDrawer(modalDrawer);
              }
            });
  }

  /* Sets modal navigation drawer's header button to close the drawer. */
  private static void setModalDrawerButtonOnClickListener(
          @NonNull DrawerLayout drawerLayout,
          @NonNull View button,
          @NonNull NavigationView modalDrawer) {
    button.setOnClickListener(
            new OnClickListener() {
              @Override
              public void onClick(View v) {
                drawerLayout.closeDrawer(modalDrawer);
              }
            });
  }
}
