package cm.aptoide.pt.billing.customer;

import cm.aptoide.pt.billing.authorization.Authorization;
import java.util.List;
import rx.Completable;
import rx.Single;

public interface AuthorizationPersistence {

  Single<List<Authorization>> getAuthorizations(String customerId);

  Completable saveAuthorization(Authorization authorization);

  Completable removeAuthorization(long authorizationId);
}