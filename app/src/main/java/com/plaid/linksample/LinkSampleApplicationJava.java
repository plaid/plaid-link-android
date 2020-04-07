package com.plaid.linksample;

import android.app.Application;

import com.plaid.link.Plaid;
import com.plaid.linkbase.models.configuration.PlaidEnvironment;
import com.plaid.linkbase.models.configuration.PlaidOptions;
import com.plaid.log.LogLevel;

public class LinkSampleApplicationJava extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    PlaidOptions plaidOptions = new PlaidOptions.Builder()
        .environment(PlaidEnvironment.SANDBOX)
        .logLevel(BuildConfig.DEBUG ? LogLevel.DEBUG : LogLevel.ASSERT)
        .build();

    Plaid.setOptions(plaidOptions);
  }
}
