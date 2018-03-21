package cm.aptoide.pt.home.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsFragment extends NavigationTrackFragment implements AppsFragmentView {

  @Inject DownloadAnalytics downloadAnalytics;
  @Inject InstallAnalytics installAnalytics;
  @Inject NavigationTracker navigationTracker;
  private RecyclerView recyclerView;
  private AppsAdapter adapter;
  private PublishSubject<AppClick> appItemClicks;

  public static AppsFragment newInstance() {
    return new AppsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    appItemClicks = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    recyclerView = (RecyclerView) view.findViewById(R.id.fragment_apps_recycler_view);

    List<App> appsList = new ArrayList<>();
    appsList.add(
        new DownloadsHeader(getResources().getString(R.string.apps_title_downloads_header)));
    appsList.add(new UpdatesHeader(getResources().getString(R.string.apps_title_updates_header)));
    appsList.add(
        new InstalledHeader(getResources().getString(R.string.apps_title_installed_apps_header)));

    adapter = new AppsAdapter(appsList, new AppCardViewHolderFactory(appItemClicks));

    setupRecyclerView();

    attachPresenter(new AppsPresenter(this, new AppsManager(new UpdatesManager(
        RepositoryFactory.getUpdateRepository(getContext(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
        ((AptoideApplication) getContext().getApplicationContext()).getInstallManager(),
        new InstallToDownloadAppMapper(), new InstalledToInstalledAppMapper(), downloadAnalytics,
        installAnalytics, getContext().getPackageManager(), getContext()),
        AndroidSchedulers.mainThread(), Schedulers.computation(), CrashReport.getInstance(),
        new PermissionManager(), ((PermissionService) getContext())));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void setupRecyclerView() {
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_apps, container, false);
  }

  @Override public void showUpdatesList(List<App> list) {
    list.add(new UpdatesHeader(getResources().getString(R.string.apps_title_updates_header)));
    adapter.addUpdateAppsList(list);
  }

  @Override public void showInstalledApps(List<App> installedApps) {
    adapter.addInstalledAppsList(installedApps);
  }

  @Override public void showDownloadsList(List<App> list) {
    if (list != null && !list.isEmpty()) {
      adapter.addDownloadAppsList(list);
    }
  }

  @Override public Observable<App> retryDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RETRY_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> installApp() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.INSTALL_APP)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> cancelDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.CANCEL_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> resumeDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RESUME_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> pauseDownload() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.PAUSE_DOWNLOAD)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> retryUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RETRY_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> updateApp() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.UPDATE_APP)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> pauseUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.PAUSE_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> cancelUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.CANCEL_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public Observable<App> resumeUpdate() {
    return appItemClicks.filter(
        appClick -> appClick.getClickType() == AppClick.ClickType.RESUME_UPDATE)
        .map(appClick -> appClick.getApp());
  }

  @Override public void onDestroy() {
    appItemClicks = null;
    super.onDestroy();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    recyclerView = null;
    adapter = null;
  }
}