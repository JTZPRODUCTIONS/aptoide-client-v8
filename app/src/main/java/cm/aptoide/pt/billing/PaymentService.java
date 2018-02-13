package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.customer.AuthorizationPersistence;
import cm.aptoide.pt.billing.transaction.Transaction;
import rx.Single;

public interface PaymentService<T> {

  public Single<? extends Transaction> pay(String customerId, long productId, long authorizationId,
      BillingService billingService, String payload);

  public Single<? extends Authorization> authorize(String customerId, T metadata,
      AuthorizationPersistence authorizationPersistence, BillingService authorizationService,
      long paymentMethodId);
}