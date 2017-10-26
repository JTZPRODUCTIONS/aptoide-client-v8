package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.message.FriendsManager;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Accepter;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.HotspotManager;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareandroid.util.service.ServiceProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by neuro on 19-06-2017.
 */

public class SpotAndShare {

  public static final String APTOIDE_HOTSPOT = "AptoideHotspot";
  private final String PASSWORD_APTOIDE = "passwordAptoide";
  private final HotspotManager hotspotManager;
  private final SpotAndShareV2 spotAndShareV2;
  private Map<AndroidAppInfo, Accepter<AndroidAppInfo>> androidAppInfoAccepterMap = new HashMap<>();
  private final ServiceProvider serviceProvider;
  private FriendsManager friendsManager;

  public SpotAndShare(Context context, Friend friend) {
    serviceProvider = new ServiceProvider(context);
    hotspotManager = new HotspotManager(context, (WifiManager) context.getApplicationContext()
        .getSystemService(Context.WIFI_SERVICE), serviceProvider.getWifiManager());
    this.friendsManager = new FriendsManager();
    spotAndShareV2 = new SpotAndShareV2(context, friend, this.friendsManager);
  }

  public Completable createGroup(Action1<SpotAndShareSender> onSuccess, OnError onError,
      AndroidAppInfoAccepter androidAppInfoAccepter) {
    if (spotAndShareV2.isHotspot()) {
      return Completable.complete();
    }
    return spotAndShareV2.send(onSuccess, onError::onError);
  }

  public Completable createOpenGroup(Action1<Void> onSuccess) {
    return spotAndShareV2.enableOpenHotspot(onSuccess, APTOIDE_HOTSPOT);
  }

  public void isGroupCreated(GroupCreated groupCreated) {
    // TODO: 19-06-2017 neuro
    groupCreated.isCreated(false);//added this in order to test the hotspot creation
  }

  public void joinGroup(Action1<SpotAndShareSender> onSuccess, OnError onError) {
    spotAndShareV2.receive(onSuccess, onError::onError);
  }

  public void leaveGroup(Action1<? super Throwable> onError) {
    friendsManager.clearFriends();
    spotAndShareV2.exit(onError);
  }

  public void sendApps(List<AndroidAppInfo> appsList) {
    // TODO: 19-06-2017 neuro
    spotAndShareV2.sendApps(appsList);
  }

  public boolean canSend() {
    return spotAndShareV2.canSend();
  }

  public Observable<List<Transfer>> observeTransfers() {
    return spotAndShareV2.observeTransfers();
  }

  public Observable<Collection<Friend>> observeFriends() {
    //return spotAndShareV2.observeFriends();
    return friendsManager.observe();
  }

  public Observable<Integer> observeAmountOfFriends() {
    //return spotAndShareV2.observeAmountOfFriends();
    return friendsManager.observeAmountOfFriends();
  }

  public interface GroupCreated {
    void isCreated(boolean created);
  }

  public interface OnSuccess {
    void onSuccess(String uuid);
  }

  public interface OnError {
    void onError(Throwable throwable);
  }

  public interface AcceptReception {
    boolean accept(AndroidAppInfo androidAppInfo);
  }
}
