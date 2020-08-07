package com.group19.softwareengineeringproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SubscriptionItem implements Parcelable {
  protected SubscriptionItem (Parcel in) {
    in.readStringList(this.subscriptions);
  }

  public static final Creator<SubscriptionItem> CREATOR = new Creator<SubscriptionItem>() {
    @Override
    public SubscriptionItem createFromParcel(Parcel in) {
      return new SubscriptionItem(in);
    }

    @Override
    public SubscriptionItem[] newArray(int size) {
      return new SubscriptionItem[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeList(this.subscriptions);
  }

  private List<String> subscriptions;

  public List<String> getList() {
    return subscriptions;
  }

  public SubscriptionItem (List<String> subscriptions) {
    this.subscriptions = subscriptions;
  }

}
