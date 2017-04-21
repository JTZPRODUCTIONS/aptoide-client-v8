package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import lombok.Setter;

/**
 * Created by trinkes on 05/01/2017.
 */

public class InstallEvent extends DownloadInstallBaseEvent {
  private static final String EVENT_NAME = "INSTALL";
  @Setter private boolean aptoideSettings;
  @Setter private boolean isPhoneRooted;

  public InstallEvent(Action action, Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, AppContext context, int versionCode,
      DownloadInstallEventConverter downloadInstallEventConverter,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(action, origin, packageName, url, obbUrl, patchObbUrl, context, versionCode,
        downloadInstallEventConverter, EVENT_NAME, bodyInterceptor);
  }

  public boolean getAptoideSettings() {
    return aptoideSettings;
  }

  public boolean getIsPhoneRooted() {
    return isPhoneRooted;
  }
}
