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
import com.plaid.linkbase.models.LinkCancellation;
import com.plaid.linkbase.models.LinkConfiguration;
import com.plaid.linkbase.models.LinkConnection;
import com.plaid.linkbase.models.LinkConnectionMetadata;
import com.plaid.linkbase.models.LinkEventListener;
import com.plaid.linkbase.models.PlaidApiError;
import com.plaid.linkbase.models.PlaidProduct;

import kotlin.Unit;

import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainJavaActivity extends AppCompatActivity {

  private static final int LINK_REQUEST_CODE = 1;
  private TextView contentTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_java);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    contentTextView = findViewById(R.id.content);

    FloatingActionButton fab = findViewById(R.id.open_link_fab);
    fab.setOnClickListener(view -> {
      Plaid.setLinkEventListener(new LinkEventListener(it -> {
        Log.i("Event", it.toString());
        return Unit.INSTANCE;
      }));
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
    if (requestCode == LINK_REQUEST_CODE && data != null) {
      if (resultCode == Plaid.RESULT_SUCCESS) {
        LinkConnection item = (LinkConnection) data.getSerializableExtra(Plaid.LINK_RESULT);
        if (item != null) {
          LinkConnectionMetadata metadata = item.getLinkConnectionMetadata();
          contentTextView.setText(getString(
              R.string.content_success,
              item.getPublicToken(),
              metadata.getAccounts().get(0).getAccountId(),
              metadata.getAccounts().get(0).getAccountName(),
              metadata.getInstitutionId(),
              metadata.getInstitutionName()));
        }
      } else if (resultCode == Plaid.RESULT_CANCELLED) {
        LinkCancellation cancellation = (LinkCancellation) data.getSerializableExtra(Plaid.LINK_RESULT);
        if (cancellation != null) {
          contentTextView.setText(getString(
              R.string.content_cancelled,
              cancellation.getInstitutionId(),
              cancellation.getInstitutionName(),
              cancellation.getLinkSessionId(),
              cancellation.getStatus()));
        }
      } else if (resultCode == Plaid.RESULT_EXIT) {
        PlaidApiError error = (PlaidApiError) data.getSerializableExtra(Plaid.LINK_RESULT);
        if (error != null) {
          contentTextView.setText(getString(
              R.string.content_exit,
              error.getDisplayMessage(),
              error.getErrorCode(),
              error.getErrorMessage(),
              error.getLinkExitMetadata().getInstitutionId(),
              error.getLinkExitMetadata().getInstitutionName(),
              error.getLinkExitMetadata().getStatus()));
        }
      } else if (resultCode == Plaid.RESULT_EXCEPTION) {
        Exception exception = (Exception) data.getSerializableExtra(Plaid.LINK_RESULT);
        if (exception != null) {
          contentTextView.setText(getString(
              R.string.content_exception,
              exception.getClass().toString(),
              exception.getMessage()));
        }
      }
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
