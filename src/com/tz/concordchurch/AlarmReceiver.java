package com.tz.concordchurch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
	  System.out.println("");
//    if (ContactsApi.getContactsApi().isContactFullyPopulated()
//        && ContactsApi.getContactsApi().isContactListChanged()) {
      new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
//          ContactsApi.getContactsApi().updateListOfAllContacts(true);
          return null;
        }
      }.execute();
//    }
  }

}