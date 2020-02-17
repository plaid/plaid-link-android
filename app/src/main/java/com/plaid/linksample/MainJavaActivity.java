package com.plaid.linksample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.plaid.link.Plaid;
import com.plaid.linkbase.models.configuration.LinkConfiguration;
import com.plaid.linkbase.models.configuration.PlaidProduct;
import com.plaid.linkbase.models.connection.LinkConnection;
import com.plaid.linkbase.models.connection.PlaidLinkResultHandler;

import kotlin.Unit;

import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainJavaActivity extends AppCompatActivity {

  private static final int LINK_REQUEST_CODE = 1;
  private TextView contentTextView;

  private PlaidLinkResultHandler plaidLinkActivityResultHandler = new PlaidLinkResultHandler(
      LINK_REQUEST_CODE,
      linkConnection -> {
        LinkConnection.LinkConnectionMetadata metadata = linkConnection.getLinkConnectionMetadata();
        contentTextView.setText(getString(
            R.string.content_success,
            linkConnection.getPublicToken(),
            metadata.getAccounts().get(0).getAccountId(),
            metadata.getAccounts().get(0).getAccountName(),
            metadata.getInstitutionId(),
            metadata.getInstitutionName()));
        return Unit.INSTANCE;
      },
      linkCancellation -> {
        contentTextView.setText(getString(
            R.string.content_cancelled,
            linkCancellation.getInstitutionId(),
            linkCancellation.getInstitutionName(),
            linkCancellation.getLinkSessionId(),
            linkCancellation.getStatus()));
        return Unit.INSTANCE;
      },
      plaidApiError -> {
        contentTextView.setText(getString(
            R.string.content_exit,
            plaidApiError.getDisplayMessage(),
            plaidApiError.getErrorCode(),
            plaidApiError.getErrorMessage(),
            plaidApiError.getLinkExitMetadata().getInstitutionId(),
            plaidApiError.getLinkExitMetadata().getInstitutionName(),
            plaidApiError.getLinkExitMetadata().getStatus()));
        return Unit.INSTANCE;
      }
  );

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_java);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    contentTextView = findViewById(R.id.content);

    FloatingActionButton fab = findViewById(R.id.open_link_fab);
    fab.setOnClickListener(view -> {
      Plaid.setLinkEventListener(linkEvent -> {
        Log.i("Event", linkEvent.toString());
        return Unit.INSTANCE;
      });
      ArrayList<PlaidProduct> products = new ArrayList<>();
      products.add(PlaidProduct.TRANSACTIONS);
      Plaid.openLink(
          MainJavaActivity.this,
          new LinkConfiguration.Builder("Test App", products).build(),
          LINK_REQUEST_CODE);
    });
  }

  @Override
  protected void onActivityResult(
      int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (!plaidLinkActivityResultHandler.onActivityResult(requestCode, resultCode, data)) {
      Log.i(MainJavaActivity.class.getSimpleName(), "Not handled");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.java_menu, menu);
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
