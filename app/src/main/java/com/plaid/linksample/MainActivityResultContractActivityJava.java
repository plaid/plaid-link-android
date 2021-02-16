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
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plaid.link.OpenPlaidLink;
import com.plaid.link.Plaid;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkExit;
import com.plaid.link.result.LinkSuccess;
import com.plaid.linksample.network.LinkTokenRequester;

import kotlin.Unit;


public class MainActivityResultContractActivityJava extends AppCompatActivity {

  private TextView result;
  private TextView tokenResult;

  // Experimental API using ActivityResultContract for androidx.fragment:1.3.0+
  private ActivityResultLauncher<LinkTokenConfiguration> openPlaidLink =
      this.registerForActivityResult(
          new OpenPlaidLink(),
          linkResult -> {
            if (linkResult instanceof LinkSuccess) {
              String tokenString = ((LinkSuccess) linkResult).getPublicToken();
              tokenResult.setText(getString(
                  R.string.public_token_result,
                  tokenString));
              result.setText(getString(
                  R.string.content_success));
            } else if (linkResult instanceof LinkExit) {
              LinkExit linkExit = (LinkExit) linkResult;
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
            } else {
              throw new RuntimeException("Got unexpected result:" + result);
            }
          });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    result = findViewById(R.id.result);
    tokenResult = findViewById(R.id.public_token_result);

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
    LinkTokenRequester.INSTANCE.getToken()
        .subscribe(this::onLinkTokenSuccess, this::onLinkTokenError);
  }

  private void onLinkTokenSuccess(String token) {
    // Experimental API using ActivityResultContract for androidx.fragment:1.3.0+
    openPlaidLink.launch(new LinkTokenConfiguration.Builder()
        .token(token)
        .build());
  }

  private void onLinkTokenError(Throwable error) {
    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
