package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUserManager;
import cm.aptoide.pt.spotandshareapp.SpotAndSharePermissionProvider;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragmentView;
import cm.aptoide.pt.view.permission.PermissionProvider;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragmentPresenter implements Presenter {
  public static final int EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SHARE_APTOIDE = 2;
  public static final int WRITE_SETTINGS_REQUEST_CODE_SEND = 3;
  public static final int WRITE_SETTINGS_REQUEST_CODE_RECEIVE = 4;
  public static final int WRITE_SETTINGS_REQUEST_CODE_SHARE_APTOIDE = 5;

  private SpotAndShareLocalUserManager spotAndShareUserManager;
  private SpotAndSharePermissionProvider spotAndSharePermissionProvider;
  private SpotAndShareMainFragmentView view;
  private final PermissionManager permissionManager;
  private final PermissionService permissionService;

  public SpotAndShareMainFragmentPresenter(SpotAndShareMainFragmentView view,
      SpotAndShareLocalUserManager spotAndShareUserManager,
      SpotAndSharePermissionProvider spotAndSharePermissionProvider,
      PermissionManager permissionManager, PermissionService permissionService) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
    this.spotAndSharePermissionProvider = spotAndSharePermissionProvider;
    this.permissionManager = permissionManager;
    this.permissionService = permissionService;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> view.startSend())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.openAppSelectionFragment(true))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> view.startReceive())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.openWaitingToReceiveFragment();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> view.editProfile())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.openEditProfile();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());

    loadProfileInformationOnView();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.shareAptoideApk())
        .doOnNext(__ -> {
          view.openShareAptoideFragment();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private SpotAndShareLocalUser getSpotAndShareProfileInformation() {
    return spotAndShareUserManager.getUser();
  }

  private void loadProfileInformationOnView() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.loadProfileInformation(getSpotAndShareProfileInformation()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void handleLocationAndExternalStoragePermissionsResult() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(
            __ -> spotAndSharePermissionProvider.locationAndExternalStoragePermissionsResultSpotAndShare(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SHARE_APTOIDE)
                .filter(permissions -> {
                  for (PermissionProvider.Permission permission : permissions) {
                    if (!permission.isGranted()) {
                      return false;
                    }
                  }
                  return true;
                })
                .doOnNext(permissions -> {
                  spotAndSharePermissionProvider.requestWriteSettingsPermission(
                      WRITE_SETTINGS_REQUEST_CODE_SHARE_APTOIDE);
                }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void handleWriteSettingsPermissionResult() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> spotAndSharePermissionProvider.writeSettingsPermissionResult())
        .filter(requestCode -> requestCode == WRITE_SETTINGS_REQUEST_CODE_SHARE_APTOIDE)
        .doOnNext(requestCode -> {
          view.openShareAptoideFragment();
        })
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }
}