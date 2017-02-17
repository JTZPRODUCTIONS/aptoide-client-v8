package cm.aptoide.pt.shareapppsandroid;

import java.util.List;

/**
 * Created by filipegoncalves on 09-02-2017.
 */

public interface HighwayTransferRecordView {

  void setUpSendButtonListener();

  void setUpClearHistoryListener();

  void handleReceivedApp(boolean received, boolean needReSend, String tmpFilePath);

  void showNewCard(HighwayTransferRecordItem item);

  void showNoConnectedClientsToast();

  void openAppSelectionView();

  void showNoRecordsToDeleteToast();

  void showDeleteHistoryDialog();

  void refreshAdapter();

  void hideReceivedAppMenu();

  void showInstallErrorDialog(String appName);

  void showDialogToInstall(String appName, String filePath);

  void showDialogToDelete(HighwayTransferRecordItem item);

  void setAdapterListeners(TransferRecordListener listener);

  void notifyChanged();

  void generateAdapter(List<HighwayTransferRecordItem> list);

  interface TransferRecordListener {
    void onInstallApp(HighwayTransferRecordItem item);

    void onDeleteApp(HighwayTransferRecordItem item);

    void onReSendApp(HighwayTransferRecordItem item, int position);
  }
}
