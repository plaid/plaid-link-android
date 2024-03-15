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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.plaid.link.Plaid;
import com.plaid.link.PlaidHandler;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkResultHandler;
import com.plaid.linksample.network.LinkTokenRequester;
import kotlin.Unit;

/**
 * Old approach to opening Plaid Link, we recommend switching over to the
 * OpenPlaidLink ActivityResultContract instead.
 */
public class MainActivityStartActivityForResultJava extends AppCompatActivity {

  private TextView result;
  private TextView tokenResult;
  private MaterialButton prepareButton;
  private MaterialButton openButton;
  private PlaidHandler plaidHandler;

  private LinkResultHandler myPlaidResultHandler = new LinkResultHandler(
      linkSuccess -> {
        tokenResult.setText(getString(
            R.string.public_token_result,
            linkSuccess.getPublicToken()));
        result.setText(getString(
            R.string.content_success));
        return Unit.INSTANCE;
      },
      linkExit -> {
        tokenResult.setText("");
        if (linkExit.getError() != null) {
          result.setText(getString(
              R.string.content_exit,
              linkExit.getError().getDisplayMessage(),
              linkExit.getError().getErrorCode()));
        } else {
          result.setText(getString(
              R.string.content_cancel,
              linkExit.getMetadata().getStatus() != null ? linkExit.getMetadata()
                  .getStatus()
                  .getJsonValue() : "unknown"));
        }
        return Unit.INSTANCE;
      }
  );

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    result = findViewById(R.id.result);
    tokenResult = findViewById(R.id.public_token_result);

    prepareButton = findViewById(R.id.prepare_link);
    prepareButton.setOnClickListener(view -> {
      setOptionalEventListener();
      prepareLink();
    });

    openButton = findViewById(R.id.open_link);
    openButton.setOnClickListener(view -> {
      openLink();
    });
  }

  private void prepareLink() {
    LinkTokenRequester.INSTANCE.getToken()
        .subscribe(this::onLinkTokenSuccess, this::onLinkTokenError);
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
    prepareButton.setEnabled(true);
    openButton.setEnabled(false);
    plaidHandler.open(this);
  }

  private void onLinkTokenSuccess(String token) {
    prepareButton.setEnabled(false);
    openButton.setEnabled(true);
    plaidHandler = Plaid.create(
        getApplication(),
        new LinkTokenConfiguration.Builder()
            .token(token)
            .build());
  }

  private void onLinkTokenError(Throwable error) {
    if (error instanceof java.net.ConnectException) {
      Toast.makeText(
          this,
          "Please run `sh start_server.sh <client_id> <sandbox_secret>`",
          Toast.LENGTH_LONG).show();
      return;
    }
    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
