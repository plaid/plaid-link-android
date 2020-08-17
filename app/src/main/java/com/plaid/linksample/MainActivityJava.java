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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.plaid.link.Plaid;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.PlaidLinkResultHandler;
import kotlin.Unit;

public class MainActivityJava extends AppCompatActivity {

  private TextView result;
  private TextView tokenResult;

  private final PlaidLinkResultHandler myPlaidResultHandler = new PlaidLinkResultHandler(
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
        if (linkExit.error != null) {
          result.setText(getString(
              R.string.content_exit,
              linkExit.error.getDisplayMessage(),
              linkExit.error.getErrorCode()));
        } else {
          result.setText(getString(
              R.string.content_cancel,
              linkExit.metadata.status != null ? linkExit.metadata.status.jsonValue : "unknown"));
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
    Plaid.openLink(
        this,
        new LinkTokenConfiguration.Builder()
            .token(getLinkTokenFromServer())
            .build()
            .toLinkConfiguration());
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

  /**
   * In production, make an API request to your server to fetch
   * a new link_token. Learn more at https://plaid.com/docs/#create-link-token.
   * <p>
   * This is a dummy implementation. If you curl for a link_token, you can
   * copy and paste the link_token value here.
   */
  private String getLinkTokenFromServer() {
    return "<GENERATED_LINK_TOKEN>";
  }
}
