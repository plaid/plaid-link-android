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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.plaid.link.Plaid;
import com.plaid.linkbase.models.configuration.LinkConfiguration;
import com.plaid.linkbase.models.configuration.PlaidProduct;
import com.plaid.linkbase.models.connection.LinkConnection;
import com.plaid.linkbase.models.connection.PlaidLinkResultHandler;
import java.util.ArrayList;
import kotlin.Unit;

public class MainActivityJava extends AppCompatActivity {

  private static final int LINK_REQUEST_CODE = 1;
  private TextView result;

  private PlaidLinkResultHandler plaidLinkActivityResultHandler = new PlaidLinkResultHandler(
      LINK_REQUEST_CODE,
      linkConnection -> {
        LinkConnection.LinkConnectionMetadata metadata = linkConnection.getLinkConnectionMetadata();
        result.setText(getString(
            R.string.content_success,
            linkConnection.getPublicToken(),
            metadata.getAccounts().get(0).getAccountId(),
            metadata.getAccounts().get(0).getAccountName(),
            metadata.getInstitutionId(),
            metadata.getInstitutionName()));
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
    result = findViewById(R.id.result);

    FloatingActionButton fab = findViewById(R.id.open_link);
    fab.setOnClickListener(view -> {
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

  private void openLink() {
    ArrayList<PlaidProduct> products = new ArrayList<>();
    products.add(PlaidProduct.TRANSACTIONS);
    Plaid.openLink(
        MainActivityJava.this,
        new LinkConfiguration.Builder("Test App", products).build(),
        LINK_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (!plaidLinkActivityResultHandler.onActivityResult(requestCode, resultCode, data)) {
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
