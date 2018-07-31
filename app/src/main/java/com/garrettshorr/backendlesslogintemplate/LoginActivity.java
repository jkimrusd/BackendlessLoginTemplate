package com.garrettshorr.backendlesslogintemplate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";
    // this is needed for the startActivityForResult to categorize what task is being done
    public static final int REQUEST_CODE_USERNAME = 5; // any int is fine

    private TextView textViewNewAccount;
    private Button buttonLogin;
    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region Wiring Widgets & Setting Listeners
        wireWidgets();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // having the separate login method makes it easier to read
                // what this button click listener is doing
                login();
            }
        });

        textViewNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an activity for a result & register an account
                registerNewAccount();
            }
        });
        //endregion

        //region Backendless Setup
        // initial handshake to establish this app belongs to you and you have a right to change
        // stuff. This must be done ONCE in your app before you can do any other backendless
        // operations.
        Backendless.initApp( this,
                Defaults.APP_ID,
                Defaults.API_KEY );
        //endregion

    }

    private void login() {
        // Sample method taken directly from the backendless User Service API documentation
        // then filled with the data we need (username & password)

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        // always use the asynchronous methods from the API because
        // we don't want to block the UI thread (which causes jankiness)
        Backendless.UserService.login( username, password, new AsyncCallback<BackendlessUser>()
        {
            // if it is successful
            public void handleResponse( BackendlessUser user )
            {
                // user has been logged in
                String serverUsername = (String) Backendless.UserService.CurrentUser().getProperty("username");
                Toast.makeText(LoginActivity.this, "Success! " + serverUsername + " logged in!",
                        Toast.LENGTH_SHORT).show();
            }

            // if there is a failure
            public void handleFault( BackendlessFault fault )
            {
                // login failed, to get the error code call fault.getCode()
                Toast.makeText(LoginActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerNewAccount() {
        Intent registerIntent = new Intent(this, NewAccountActivity.class);

        // package the username if provided
        String username = editTextUsername.getText().toString();
        if(username.length()> 0) {
            registerIntent.putExtra(EXTRA_USERNAME, username);
        }

        startActivityForResult(registerIntent, REQUEST_CODE_USERNAME);
    }

    private void wireWidgets() {
        textViewNewAccount = findViewById(R.id.textview_create_account);
        buttonLogin = findViewById(R.id.button_login);
        editTextPassword = findViewById(R.id.edittext_password);
        editTextUsername = findViewById(R.id.edittext_username);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // make sure you are handling the right request and also that it was successful
        if(requestCode == REQUEST_CODE_USERNAME && resultCode == RESULT_OK) {
            String username = data.getStringExtra(EXTRA_USERNAME);
            if(username != null) {
                editTextUsername.setText((username));
            }
        }
    }
}
