package in.co.ashclan.mirchithunder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import in.co.ashclan.mirchithunder.model.ParticipantModel;
import in.co.ashclan.mirchithunder.utils.util;
import info.hoang8f.widget.FButton;

public class ParticipantsLogin extends AppCompatActivity
        implements View.OnClickListener,DatePickerDialog.OnDateSetListener{

    FButton btn_facebook,btn_Gmail;
    FirebaseDatabase database;
    DatabaseReference table_participant ;
    Context mContext;
    android.app.DatePickerDialog datePickerDialog ;

    //Participant Pojo
    ParticipantModel participantModel;

    //FireBase to Facebook
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignIn";

    //FireBase to Google
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    public static final int REQUEST_CODE = 7171;


    //Alert Dialog View
    MaterialEditText edtFirstName,edtLastName,edtEmailId,edtMobileNo,edtDateofBirth;
    RadioGroup rdg_Gender;
    RadioButton rd_male,rd_female;
    Button btn_Select,btn_upload;
    private int mYear, mMonth, mDay, mHour, mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_login);
        init();
        btn_Gmail.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void init() {
        mContext = ParticipantsLogin.this;
        btn_facebook = (FButton)findViewById(R.id.btn_facebook);
        btn_Gmail = (FButton)findViewById(R.id.btn_Gmail);

        //InIt FireBase
        database = FirebaseDatabase.getInstance();
        table_participant = database.getReference("Participant");//Linked to Participant table
        mAuth = FirebaseAuth.getInstance();

        participantModel = new ParticipantModel();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_facebook:
                startFacebookLogin();
                break;
            case R.id.btn_Gmail:
                startGmailLogin();
                break;
        }
    }
    private void startFacebookLogin() {

        Intent intent = new Intent(ParticipantsLogin.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }
    private void startGmailLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(result.getError()!=null)
            {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }else if(result.wasCancelled())
            {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                if(result.getAccessToken()!=null)
                {
                    //Show Dialog
                    final AlertDialog watingDialog = new SpotsDialog(this);
                    watingDialog.show();
                    watingDialog.setMessage("Please Wait");
                    watingDialog.setCancelable(false);
                    //get Current Phone
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String userphone = account.getPhoneNumber().toString();

                            //Check if User Exist on Firebase
                            table_participant.orderByKey().equalTo(userphone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.child(userphone).exists())//if Usernot Exist
                                            {
                                                ParticipantModel newUser = new ParticipantModel();
                                                newUser.setMobile(userphone);

                                                //add to fire base
                                                table_participant.child(userphone)
                                                        .setValue(newUser)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                    Toast.makeText(ParticipantsLogin.this, "User Register Successfully !", Toast.LENGTH_SHORT).show();

                                                                //Login
                                                                table_participant.child(userphone)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                                ParticipantModel localUser = dataSnapshot.getValue(ParticipantModel.class);
                                                                                startActivity(new Intent(ParticipantsLogin.this, QRCodeReaderActivity.class));
                                                                                util.currentParticipant = localUser;
                                                                                watingDialog.dismiss();
                                                                                finish();
                                                                            }
                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {
                                                                            }
                                                                        });
                                                            }
                                                        });

                                            }else//if User Exist
                                            {
                                                table_participant.child(userphone)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                ParticipantModel localUser = dataSnapshot.getValue(ParticipantModel.class);
                                                                startActivity(new Intent(ParticipantsLogin.this, QRCodeReaderActivity.class));
                                                                util.currentParticipant = localUser;
                                                                watingDialog.dismiss();
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                        }
                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(ParticipantsLogin.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            showFireBaseDialog();
//                            startActivity(new Intent(ParticipantsLogin.this,QRCodeReaderActivity.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                    private void updateUI(FirebaseUser user) {
                    }
                });
    }

    private void showFireBaseDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ParticipantsLogin.this);
        alertDialog.setTitle("One More Step... ");
        alertDialog.setMessage("Please Fill all Information");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_register_dialog,null);

        edtFirstName    = (MaterialEditText) view.findViewById(R.id.edt_FirstName);
        edtLastName     = (MaterialEditText) view.findViewById(R.id.edt_LastName);
        edtMobileNo     = (MaterialEditText) view.findViewById(R.id.edt_MobileNo);
        edtEmailId      = (MaterialEditText) view.findViewById(R.id.edt_EmailId);
        edtDateofBirth  = (MaterialEditText)view.findViewById(R.id.edt_DateOfBirth);

        btn_Select      = (Button)view.findViewById(R.id.btnSelect);
        btn_upload      = (Button)view.findViewById(R.id.btnUpload);

        rd_male         = (RadioButton)view.findViewById(R.id.rd_male);
        rd_female       = (RadioButton)view.findViewById(R.id.rd_female);

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_person);

        datePickerDialog = new DatePickerDialog(mContext,ParticipantsLogin.this, startYear, starthMonth, startDay);

        //Event for Material Edit text
        edtDateofBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog.show();

            }
        });

        //Set Buttons
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               /* if(newFood!=null)
                {
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootlayout, "Food Item "+newFood.getName().toString()+" Added successfully", Snackbar.LENGTH_SHORT).show();
                }*/
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }
}
