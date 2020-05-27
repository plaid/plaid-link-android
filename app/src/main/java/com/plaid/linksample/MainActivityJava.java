/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plaid.link.Plaid;
import com.plaid.linkbase.models.configuration.LinkConfiguration;
import com.plaid.linkbase.models.configuration.PlaidProduct;
import com.plaid.linkbase.models.connection.PlaidLinkResultHandler;
import com.plaid.linksample.network.LinkSampleApi;

import kotlin.Unit;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivityJava extends AppCompatActivity {

  private static final int LINK_REQUEST_CODE = 1;
  private TextView result;
  private LinkSampleApi linkSampleApi;

  private PlaidLinkResultHandler myPlaidResultHandler = new PlaidLinkResultHandler(
      LINK_REQUEST_CODE,
      linkConnection -> {
        result.setText(getString(
            R.string.content_success,
            linkConnection.getPublicToken()));
        return Unit.INSTANCE;
      },
      linkCancellation -> {
        result.setText(getString(
            R.string.content_cancelled,
            linkCancellation.getInstitutionId(),
            linkCancellation.getInstitutionName(),
            linkCancellation.getLinkSessionId(),
            linkCancellation.getStatus()));
        return Unit.INSTANCE;
      },
      plaidApiError -> {
        result.setText(getString(
            R.string.content_exit,
            plaidApiError.getErrorMessage(),
            plaidApiError.getErrorCode()));
        return Unit.INSTANCE;
      }
  );

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    result = findViewById(R.id.result);
    linkSampleApi = ((LinkSampleApplication) getApplication()).getLinkSampleApi();

    View button = findViewById(R.id.open_link);
    button.setOnClickListener(view -> {
      setOptionalEventListener();
      openLink();
    });
  }

  /**
   * Optional, set an <a href="https://plaid.com/docs/link/android/#handling-onevent">event listener</a>.
   */
  private void setOptionalEventListener() {
    Plaid.setLinkEventListener(linkEvent -> {
      Log.i("Event", linkEvent.toString());
      return Unit.INSTANCE;
    });
  }

  /**
   * For all Link configuration options, have a look at the
   * <a href="https://plaid.com/docs/link/android/#parameter-reference">parameter reference</>
   */
  private void openLink() {
    // We create an item-add-token in order to authenticate item creation.
    linkSampleApi.getItemAddToken()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(addTokenResponse -> {
          Plaid.openLink(
              MainActivityJava.this,
              new LinkConfiguration.Builder(
                  "Link demo",
                  Arrays.asList(PlaidProduct.TRANSACTIONS)).build(),
              LINK_REQUEST_CODE);
        }, error -> { Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show(); });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (!myPlaidResultHandler.onActivityResult(requestCode, resultCode, data)) {
      Log.i(MainActivityJava.class.getSimpleName(), "Not handled");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_java, menu);
    return true;
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.show_kotlin:
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
