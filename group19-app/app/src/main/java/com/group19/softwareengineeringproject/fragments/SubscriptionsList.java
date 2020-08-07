package com.group19.softwareengineeringproject.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.models.SubscriptionItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscriptionsList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscriptionsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscriptionsList extends Fragment {
  private static final String ARGS = "LIST";

  private SubscriptionItem subscriptions;
  //private OnFragmentInteractionListener mListener;

  public SubscriptionsList() {
    // Required empty public constructor
  }

  public static SubscriptionsList newInstance(SubscriptionItem subscriptions) {
    SubscriptionsList fragment = new SubscriptionsList();

    Bundle args = new Bundle();
    args.putParcelable(ARGS, subscriptions);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      subscriptions = getArguments().getParcelable(ARGS);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View layout = inflater.inflate(R.layout.societies_list, container, false);

    ListView subs = layout.findViewById(R.id.subs_list);

    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
        (this.getContext(), android.R.layout.simple_list_item_1, subscriptions.getList());

        // DataBind ListView with items from ArrayAdapter
    subs.setAdapter(arrayAdapter);
    return layout;
  }

  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    /*
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
    */
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    /*
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
    */
  }

  @Override
  public void onDetach() {
    super.onDetach();
    //mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }
}
