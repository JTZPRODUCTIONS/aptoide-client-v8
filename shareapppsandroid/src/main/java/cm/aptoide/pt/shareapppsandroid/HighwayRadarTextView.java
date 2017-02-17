package cm.aptoide.pt.shareapppsandroid;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by filipegoncalves on 30-08-2016.
 */
public class HighwayRadarTextView extends FrameLayout
    implements ViewTreeObserver.OnGlobalLayoutListener {

  private static final int MAX = 5;
  private static final int idX = 0;
  private static final int idY = 1;
  private static final int idTxtLength = 2;
  private static final int idDist = 3;
  private static final int textSize = 12;
  private Random random;
  private ArrayList<String> vetorKeywords;
  private int width;
  private int height;
  private int mode = HighwayRadarRippleView.MODE_OUT;
  private int fontColor = 0xff0000ff;
  private int shadowColor = 0xdd696969;
  private OnRippleViewClickListener onRippleOutViewClickListener;
  private List<HighwayRadarRippleView> listOfHotspot;
  private List<HighwayRadarLowElement> listOfHotspotLow;

  private HighwayActivity activity;
  private String hotspotName;

  public HighwayRadarTextView(Context context) {
    super(context);
    init(null, context);
  }

  private void init(AttributeSet attrs, Context context) {
    random = new Random();
    vetorKeywords = new ArrayList<String>(MAX);
    getViewTreeObserver().addOnGlobalLayoutListener(this);
  }

  public HighwayRadarTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, context);
  }

  public HighwayRadarTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, context);
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {//posso passar logo HighwayActivity?
    this.activity = ((HighwayActivity) activity);
  }

  @Override public void onGlobalLayout() {
    int tmpWidth = getWidth();
    int tmpHeight = getHeight();
    if (width != tmpWidth || height != tmpHeight) {
      width = tmpWidth;
      height = tmpHeight;
    }
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public void setOnRippleViewClickListener(OnRippleViewClickListener listener) {
    onRippleOutViewClickListener = listener;
  }

  public void addKeyWord(String keyword) {
    if (vetorKeywords.size() < MAX) {
      if (!vetorKeywords.contains(keyword)) {
        vetorKeywords.add(keyword);
      }
    }
  }

  public ArrayList<String> getKeyWords() {
    return vetorKeywords;
  }

  public void removeKeyWord(String keyword) {
    if (vetorKeywords.contains(keyword)) {
      vetorKeywords.remove(keyword);
    }
  }

  public void show(ArrayList<String> vetorKeywords) {
    this.removeAllViews();

    if (width > 0 && height > 0 && vetorKeywords != null && vetorKeywords.size() > 0) {
      int xCenter = width >> 1;
      int yCenter = height >> 1;
      final int size = vetorKeywords.size();
      int xItem = width / (size + 1);
      int yItem = height / (size + 1);
      LinkedList<Integer> listX = new LinkedList<>();
      LinkedList<Integer> listY = new LinkedList<>();
      for (int i = 0; i < size; i++) {
        listX.add(i * xItem);
        listY.add(i * yItem + (yItem >> 2));
      }
      LinkedList<HighwayRadarRippleView> listTxtTop = new LinkedList<>();
      LinkedList<HighwayRadarRippleView> listTxtBottom = new LinkedList<>();

      listOfHotspot = new ArrayList<HighwayRadarRippleView>();

      for (int i = 0; i < size; i++) {
        final String keyword = vetorKeywords.get(i);
        int ranColor = fontColor;
        int xy[] = randomXY(random, listX, listY, xItem);
        int txtSize = textSize;

        final HighwayRadarRippleView txt = new HighwayRadarRippleView(getContext());
        if (mode == HighwayRadarRippleView.MODE_IN) {
          txt.setMode(HighwayRadarRippleView.MODE_IN);
        } else {
          txt.setMode(HighwayRadarRippleView.MODE_OUT);
        }
        final String hotspotName = removeAPTXFromString(keyword);
        System.out.println("RADAR TEXT VIEW : KEYWORD IS : " + keyword);
        txt.setText(hotspotName);
        System.out.println("RADAR TEXT VIEW hotspotName is : : " + hotspotName);
        txt.setTextColor(ranColor);

        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
        txt.setShadowLayer(1, 1, 1, shadowColor);
        txt.setGravity(Gravity.CENTER);
        txt.setOnClickListener(new OnClickListener() {
          @Override public void onClick(View view) {
            System.out.println(
                "HgihwayRadarTextView - not a textview but a framelayout, just clicked on a rippleview from the radar");
            //                        hotspotName=keyword;
            String aux = activity.getChosenHotspot();
            System.out.println("o aux/chosen hotspot ta a : " + aux);
            System.out.println("o o hotspot keyword : " + keyword);
            if (!activity.isJoinGroupFlag()) {
              if (aux.equals(keyword)) {//se o chosen hotspot ja for este
                //deseleciona-o
                deselectHotspot(keyword);
              } else {
                if (aux != "") {
                  //deselecionar a antiga tmb
                  deselectHotspot(aux);
                }
                //select o novo
                activity.setChosenHotspot(keyword);
                txt.setShadowLayer(1, 1, 1, 0xddff9800);
                txt.setTextColor(0xffff9800);

                if (size == 1) {
                  activity.joinSingleHotspot();
                }
              }
            }
          }
        });

        txt.startRippleAnimation();

        int strWidth = txt.getMeasuredWidth();
        xy[idTxtLength] = strWidth;
        if (xy[idX] + strWidth > width - (xItem)) {
          int baseX = width - strWidth;
          xy[idX] = baseX - xItem + random.nextInt(xItem >> 1);
        } else if (xy[idX] == 0) {
          xy[idX] = Math.max(random.nextInt(xItem), xItem / 3);
        }

        xy[idDist] = Math.abs(xy[idY] - yCenter);
        txt.setTag(xy);

        if (xy[idY] > yCenter) {
          listTxtBottom.add(txt);
        } else {
          listTxtTop.add(txt);
        }

        listOfHotspot.add(txt);//list para o select e unselect
      }

      attach2Screen(listTxtTop, xCenter, yCenter, yItem);
      attach2Screen(listTxtBottom, xCenter, yCenter, yItem);
    }
  }

  private int[] randomXY(Random ran, LinkedList<Integer> listX, LinkedList<Integer> listY,
      int xItem) {
    int[] arr = new int[4];
    arr[idX] = listX.remove(ran.nextInt(listX.size()));
    arr[idY] = listY.remove(ran.nextInt(listY.size()));
    return arr;
  }

  private String removeAPTXFromString(String keyword) {
    String[] array = keyword.split("_");
    String deviceName = array[2];//0 is aptx, 1 is the random chars
    return deviceName;
  }

  public void deselectHotspot(String keyword) {
    String aux = removeAPTXFromString(keyword);
    for (int i = 0; i < listOfHotspot.size(); i++) {
      if (listOfHotspot.get(i).getText().toString().equals(aux)) {
        activity.setChosenHotspot("");
        activity.setJoinGroupFlag(false);
        listOfHotspot.get(i).setShadowLayer(1, 1, 1, shadowColor);//meter a cor antiga
        listOfHotspot.get(i).setTextColor(fontColor);
      }
    }
  }

  private void attach2Screen(LinkedList<HighwayRadarRippleView> listTxt, int xCenter, int yCenter,
      int yItem) {
    int size = listTxt.size();
    sortXYList(listTxt, size);
    for (int i = 0; i < size; i++) {
      HighwayRadarRippleView txt = listTxt.get(i);
      int[] iXY = (int[]) txt.getTag();
      int yDistance = iXY[idY] - yCenter;
      int yMove = Math.abs(yDistance);
      inner:
      for (int k = i - 1; k >= 0; k--) {
        int[] kXY = (int[]) listTxt.get(k).getTag();
        int startX = kXY[idX];
        int endX = startX + kXY[idTxtLength];
        if (yDistance * (kXY[idY] - yCenter) > 0) {
          if (isXMixed(startX, endX, iXY[idX], iXY[idX] + iXY[idTxtLength])) {
            int tmpMove = Math.abs(iXY[idY] - kXY[idY]);
            if (tmpMove > yItem) {
              yMove = tmpMove;
            } else if (yMove > 0) {
              yMove = 0;
            }
            break inner;
          }
        }
      }

      if (yMove > yItem) {
        int maxMove = yMove - yItem;
        int randomMove = random.nextInt(maxMove);
        int realMove = Math.max(randomMove, maxMove >> 1) * yDistance / Math.abs(yDistance);
        iXY[idY] = iXY[idY] - realMove;
        iXY[idDist] = Math.abs(iXY[idY] - yCenter);
        sortXYList(listTxt, i + 1);
      }
      FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(200, 200);
      layParams.gravity = Gravity.LEFT | Gravity.TOP;
      layParams.leftMargin = iXY[idX];
      layParams.topMargin = iXY[idY];
      addView(txt, layParams);
    }
  }

  private void sortXYList(LinkedList<HighwayRadarRippleView> listTxt, int endIdx) {
    for (int i = 0; i < endIdx; i++) {
      for (int k = i + 1; k < endIdx; k++) {
        if (((int[]) listTxt.get(k).getTag())[idDist] < ((int[]) listTxt.get(i).getTag())[idDist]) {
          HighwayRadarRippleView iTmp = listTxt.get(i);
          HighwayRadarRippleView kTmp = listTxt.get(k);
          listTxt.set(i, kTmp);
          listTxt.set(k, iTmp);
        }
      }
    }
  }

  private boolean isXMixed(int startA, int endA, int startB, int endB) {
    boolean result = false;
    if (startB >= startA && startB <= endA) {
      result = true;
    } else if (endB >= startA && endB <= endA) {
      result = true;
    } else if (startA >= startB && startA <= endB) {
      result = true;
    } else if (endA >= startB && endA <= endB) {
      result = true;
    }

    return result;
  }

  public void changeShadowColor(int newColor) {
    this.shadowColor = newColor;
  }

  public String getPressedKeyWord() {
    return hotspotName;
  }

  public void showForLowerVersions(ArrayList<String> vetorKeywords) {
    this.removeAllViews();

    if (width > 0 && height > 0 && vetorKeywords != null && vetorKeywords.size() > 0) {
      int xCenter = width >> 1;
      int yCenter = height >> 1;
      final int size = vetorKeywords.size();
      int xItem = width / (size + 1);
      int yItem = height / (size + 1);
      LinkedList<Integer> listX = new LinkedList<>();
      LinkedList<Integer> listY = new LinkedList<>();
      for (int i = 0; i < size; i++) {
        listX.add(i * xItem);
        listY.add(i * yItem + (yItem >> 2));
      }
      LinkedList<HighwayRadarLowElement> listTxtTop = new LinkedList<>();
      LinkedList<HighwayRadarLowElement> listTxtBottom = new LinkedList<>();

      listOfHotspotLow = new ArrayList<HighwayRadarLowElement>();

      for (int i = 0; i < size; i++) {
        final String keyword = vetorKeywords.get(i);
        int ranColor = fontColor;
        int xy[] = randomXY(random, listX, listY, xItem);
        int txtSize = textSize;
        final HighwayRadarLowElement txt = new HighwayRadarLowElement(getContext());
        if (mode == HighwayRadarRippleView.MODE_IN) {
          txt.setMode(HighwayRadarRippleView.MODE_IN);
        } else {
          txt.setMode(HighwayRadarRippleView.MODE_OUT);
        }
        //                String hotspotName=removeAPTXFromString(keyword);
        txt.setText(keyword);
        txt.setTextColor(ranColor);
        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
        txt.setShadowLayer(1, 1, 1, shadowColor);
        txt.setGravity(Gravity.CENTER);
        txt.setOnClickListener(new OnClickListener() {
          @Override public void onClick(View view) {

            System.out.println(
                "HgihwayRadarTextView - not a textview but a framelayout, just clicked on a rippleview from the radar");
            //                        hotspotName=keyword;
            String aux = activity.getChosenHotspot();
            System.out.println("o aux/chosen hotspot ta a : " + aux);
            if (aux.equals(keyword)) {//se o chosen hotspot ja for este
              //deseleciona-o
              deselectHotspotLowVersion(keyword);
            } else {
              if (aux != "") {
                //deselecionar a antiga tmb
                deselectHotspotLowVersion(aux);
              }
              //select o novo
              activity.setChosenHotspot(keyword);
              txt.setShadowLayer(1, 1, 1, 0xddff9800);
              txt.setTextColor(0xffff9800);

              if (size == 1) {
                activity.joinSingleHotspot();
              }
            }
          }
        });

        int strWidth = txt.getMeasuredWidth();
        xy[idTxtLength] = strWidth;
        if (xy[idX] + strWidth > width - (xItem)) {
          int baseX = width - strWidth;
          xy[idX] = baseX - xItem + random.nextInt(xItem >> 1);
        } else if (xy[idX] == 0) {
          xy[idX] = Math.max(random.nextInt(xItem), xItem / 3);
        }

        xy[idDist] = Math.abs(xy[idY] - yCenter);
        txt.setTag(xy);

        if (xy[idY] > yCenter) {
          listTxtBottom.add(txt);
        } else {
          listTxtTop.add(txt);
        }

        listOfHotspotLow.add(txt);//list para o select e unselect
      }

      attach2ScreenLow(listTxtTop, xCenter, yCenter, yItem);
      attach2ScreenLow(listTxtBottom, xCenter, yCenter, yItem);
    }
  }

  public void deselectHotspotLowVersion(String keyword) {
    for (int i = 0; i < listOfHotspotLow.size(); i++) {
      if (listOfHotspotLow.get(i).getText().equals(keyword)) {
        activity.setChosenHotspot("");
        listOfHotspotLow.get(i).setShadowLayer(1, 1, 1, shadowColor);//meter a cor antiga
        listOfHotspotLow.get(i).setTextColor(fontColor);
      }
    }
  }

  private void attach2ScreenLow(LinkedList<HighwayRadarLowElement> listTxt, int xCenter,
      int yCenter, int yItem) {
    int size = listTxt.size();
    sortXYListLow(listTxt, size);
    for (int i = 0; i < size; i++) {
      HighwayRadarLowElement txt = listTxt.get(i);
      int[] iXY = (int[]) txt.getTag();
      int yDistance = iXY[idY] - yCenter;
      int yMove = Math.abs(yDistance);
      inner:
      for (int k = i - 1; k >= 0; k--) {
        int[] kXY = (int[]) listTxt.get(k).getTag();
        int startX = kXY[idX];
        int endX = startX + kXY[idTxtLength];
        if (yDistance * (kXY[idY] - yCenter) > 0) {
          if (isXMixed(startX, endX, iXY[idX], iXY[idX] + iXY[idTxtLength])) {
            int tmpMove = Math.abs(iXY[idY] - kXY[idY]);
            if (tmpMove > yItem) {
              yMove = tmpMove;
            } else if (yMove > 0) {
              yMove = 0;
            }
            break inner;
          }
        }
      }

      if (yMove > yItem) {
        int maxMove = yMove - yItem;
        int randomMove = random.nextInt(maxMove);
        int realMove = Math.max(randomMove, maxMove >> 1) * yDistance / Math.abs(yDistance);
        iXY[idY] = iXY[idY] - realMove;
        iXY[idDist] = Math.abs(iXY[idY] - yCenter);
        sortXYListLow(listTxt, i + 1);
      }
      FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(200, 200);
      layParams.gravity = Gravity.LEFT | Gravity.TOP;
      layParams.leftMargin = iXY[idX];
      layParams.topMargin = iXY[idY];
      addView(txt, layParams);
    }
  }

  private void sortXYListLow(LinkedList<HighwayRadarLowElement> listTxt, int endIdx) {
    for (int i = 0; i < endIdx; i++) {
      for (int k = i + 1; k < endIdx; k++) {
        if (((int[]) listTxt.get(k).getTag())[idDist] < ((int[]) listTxt.get(i).getTag())[idDist]) {
          HighwayRadarLowElement iTmp = listTxt.get(i);
          HighwayRadarLowElement kTmp = listTxt.get(k);
          listTxt.set(i, kTmp);
          listTxt.set(k, iTmp);
        }
      }
    }
  }

  public interface OnRippleViewClickListener {
    void onRippleViewClicked(View view);
  }
}

