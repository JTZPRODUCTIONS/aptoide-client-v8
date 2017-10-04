package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.sync.Sync;

public class BillingSyncFactory {

  private final Customer customer;
  private final TransactionService transactionService;
  private final AuthorizationService authorizationService;
  private final TransactionPersistence transactionPersistence;
  private final AuthorizationPersistence authorizationPersistence;

  public BillingSyncFactory(Customer customer, TransactionService transactionService,
      AuthorizationService authorizationService, TransactionPersistence transactionPersistence,
      AuthorizationPersistence authorizationPersistence) {
    this.customer = customer;
    this.transactionService = transactionService;
    this.authorizationService = authorizationService;
    this.transactionPersistence = transactionPersistence;
    this.authorizationPersistence = authorizationPersistence;
  }

  public Sync createAuthorizationSync(int paymentMethodId) {
    return new AuthorizationSync(paymentMethodId, customer, authorizationService,
        authorizationPersistence, true, true,
        BuildConfig.PAYMENT_AUTHORIZATION_SYNC_INTERVAL_MILLIS, 0);
  }

  public Sync createTransactionSync(String sellerId, Product product) {
    return new TransactionSync(product, transactionPersistence, customer, transactionService, true,
        true, BuildConfig.PAYMENT_TRANSACTION_SYNC_INTERVAL_MILLIS, 0, sellerId);
  }
}
