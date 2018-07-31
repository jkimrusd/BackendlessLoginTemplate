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
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class NewAccountActivity extends AppCompatActivity {

    private Button buttonCreate;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextEmail;
    private EditText editTextName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        wireWidgets();

        // prefill the username field from the intent

        Intent data = getIntent();
        String username = data.getStringExtra(LoginActivity.EXTRA_USERNAME);
        if(username != null) {
            editTextUsername.setText((username));
        }

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO validation checking to make sure nothing is blank
                // check for capitalization, that the passwords match, email needs to have the @ and a .

                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirm_password = editTextConfirmPassword.getText().toString();
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();

                // if passwords match and there is a username (bare minimum for success)
                if(password.equals(confirm_password) && username.length() > 0) {
                    createAccount(username, password, email, name);
                }

            }
        });
    }

    private void createAccount(final String username, String password, String email, String name) {
        // create a user and sets their properties
        BackendlessUser user = new BackendlessUser();
        // in setProperty, the first argument is the key (table column name), and the second is
        // the entry
        // the first argument has to match exactly what the column names are on backendless
        user.setProperty("email",  email);
        user.setProperty("username", username);
        user.setProperty("name", name);
        user.setPassword(password);

        // starts the async task to register the user. anything that relies upon the user
        // having been created should occur in the handle response below
        Backendless.UserService.register( user, new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse( BackendlessUser registeredUser )
            {
                // user has been registered and now can login
                Intent data = new Intent();
                data.putExtra(LoginActivity.EXTRA_USERNAME, username);
                setResult(RESULT_OK, data);

                // finish is the method that is called that will close an activity
                // you don't want to launch another activity to get back to the old one
                // because you will just have a stack of active activities
                finish();
            }

            public void handleFault( BackendlessFault fault )
            {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                Toast.makeText(NewAccountActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );

        // any code here will get executed and most likely before the handleResponse
        // or handleFailure are called


    }

    private void wireWidgets() {
        editTextPassword = findViewById(R.id.edittext_password);
        editTextUsername = findViewById(R.id.edittext_username);
        editTextConfirmPassword = findViewById(R.id.edittext_confirm_password);
        editTextName = findViewById(R.id.edittext_name);
        editTextEmail = findViewById(R.id.edittext_email);
        buttonCreate = findViewById(R.id.button_create);
    }
}
