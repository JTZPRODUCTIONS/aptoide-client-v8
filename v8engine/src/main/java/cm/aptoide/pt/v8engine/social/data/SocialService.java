package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.timeline.PackageRepository;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class SocialService {
  private final String url;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient okhttp;
  private final Converter.Factory converterFactory;
  private final PackageRepository packageRepository;
  private final int latestPackagesCount;
  private final int randomPackagesCount;
  private final TimelineResponseCardMapper mapper;
  private final LinksHandlerFactory linksHandlerFactory;

  public SocialService(String url, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient okhttp,
      Converter.Factory converterFactory, PackageRepository packageRepository,
      int latestPackagesCount, int randomPackagesCount, TimelineResponseCardMapper mapper,
      LinksHandlerFactory linksHandlerFactory) {
    this.url = url;
    this.bodyInterceptor = bodyInterceptor;
    this.okhttp = okhttp;
    this.converterFactory = converterFactory;
    this.packageRepository = packageRepository;
    this.latestPackagesCount = latestPackagesCount;
    this.randomPackagesCount = randomPackagesCount;
    this.mapper = mapper;
    this.linksHandlerFactory = linksHandlerFactory;
  }

  public Single<List<Article>> getCards(int limit, int offset) {
    return getPackages().flatMap(
        packages -> GetUserTimelineRequest.of(url, limit, offset, packages, bodyInterceptor, okhttp,
            converterFactory, null).observe().toSingle().flatMap(timelineResponse -> {
          if (timelineResponse.isOk()) {
            return Single.just(timelineResponse);
          }
          return Single.error(new IllegalStateException("Could not obtain timeline from server."));
        })).map(timelineResponse -> mapper.map(timelineResponse, linksHandlerFactory));
  }

  private Single<List<String>> getPackages() {
    return Observable.concat(packageRepository.getLatestInstalledPackages(latestPackagesCount),
        packageRepository.getRandomInstalledPackages(randomPackagesCount)).toList().toSingle();
  }
}
