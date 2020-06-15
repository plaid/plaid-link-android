/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample;

import android.app.Application;

import com.plaid.link.Plaid;

public class LinkSampleApplicationJava extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Plaid.initialize(this);
  }
}
