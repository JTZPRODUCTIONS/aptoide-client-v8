package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.PopularApp;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import rx.Observable;

class PopularAppPostShareDialog implements DialogInterface {

  private RxAlertDialog dialog;

  public PopularAppPostShareDialog(RxAlertDialog dialog) {
    this.dialog = dialog;
  }

  @Override public void cancel() {
    dialog.cancel();
  }

  @Override public void dismiss() {
    dialog.dismiss();
  }

  public void show() {
    dialog.show();
  }

  public Observable<DialogInterface> cancelsSelected() {
    return dialog.cancels();
  }

  public static class Builder {

    private final RxAlertDialog.Builder builder;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final SharePostViewSetup sharePostViewSetup;
    private final Account account;
    private PopularApp post;

    public Builder(Context context, SharePostViewSetup sharePostViewSetup, Account account) {
      this.builder = new RxAlertDialog.Builder(context);
      layoutInflater = LayoutInflater.from(context);
      this.context = context;
      this.sharePostViewSetup = sharePostViewSetup;
      this.account = account;
    }

    public PopularAppPostShareDialog.Builder setPost(PopularApp post) {
      this.post = post;
      return this;
    }

    public PopularAppPostShareDialog build() {
      View view = getView();
      sharePostViewSetup.setup(view, context, account);
      builder.setView(view);
      final RxAlertDialog dialog = builder.build();
      return new PopularAppPostShareDialog(dialog);
    }

    private View getView() {
      View view = layoutInflater.inflate(R.layout.timeline_recommendation_preview, null);
      ImageView appIcon = (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView getApp = (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
      RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

      ImageLoader.with(context)
          .load(post.getAppIcon(), appIcon);
      appName.setText(post.getAppName());
      ratingBar.setRating(post.getAppAverageRating());

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
      return view;
    }
  }
}
