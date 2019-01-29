package com.example.atik_faysal.focuslab;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by user on 11/1/2017.
 */

public class ForgetPassword extends AppCompatActivity
{
    private EditText ePhoneNumber;
    private Button bSendCode;

    private String phoneVerificationid;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthCredential credential;
    CheckInternet checkInternet;
    CountDownTimer countDownTimer;
    private String phone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_send_code);
        ePhoneNumber = (EditText)findViewById(R.id.ePhoneNumber);
        bSendCode = (Button)findViewById(R.id.bSendCode);
        firebaseAuth = FirebaseAuth.getInstance();
        checkInternet = new CheckInternet(this);
        onButtonClick();
    }

    private void onButtonClick()
    {
        bSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = ePhoneNumber.getText().toString();
               // sendVerificationCode(phone);
               // setVerificationWindow();
                ifPhoneNumberExist(phone);
            }
        });
    }

    private void sendVerificationCode(String phoneNumber)
    {
        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, verificationCallbacks);
    }

    private void setUpVerificationCallbacks()
    {
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // phoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationid = verificationId;
                resendToken = token;
            }
        };
    }

    public void verifyCode(String code)
    {
        credential = PhoneAuthProvider.getCredential(phoneVerificationid,code);
        phoneAuthCredential(credential);
    }

    private void phoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    if(phone.length()==11||phone.length()==14)
                    {
                        Intent page = new Intent(ForgetPassword.this,SetNewPassword.class);
                        page.putExtra("phone",phone);
                        startActivity(page);
                       //ifPhoneNumberExist(phone);
                    }else Toast.makeText(ForgetPassword.this,"Please Insert valid phone number",Toast.LENGTH_SHORT).show();
                }if(!checkInternet.check_internet()) Toast.makeText(ForgetPassword.this,"Please Check your Internet Connection",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgetPassword.this,"Failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void resendCode()
    {
        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(ePhoneNumber.getText().toString(), 60, TimeUnit.SECONDS, this, verificationCallbacks,resendToken);
    }

    private void setVerificationWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(ForgetPassword.this).inflate(R.layout.verification_window, null);
        builder.setTitle("One more step");
        builder.setView(view);
        Button b_verify = (Button) view.findViewById(R.id.b_verifyButton);
        TextView txtResendCode;
        final EditText e_code = (EditText) view.findViewById(R.id.e_verificationCode);
        final AlertDialog alertdialog = builder.create();
        txtResendCode = (TextView) view.findViewById(R.id.txt_resendCode);
        alertdialog.show();
        TextView timer;
        timer = (TextView)view.findViewById(R.id.timer);
        startCount(timer,alertdialog);
        b_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = e_code.getText().toString();
                if (code.isEmpty())
                    Toast.makeText(ForgetPassword.this, "Please Insert verification code", Toast.LENGTH_SHORT).show();
                else {
                    verifyCode(code);
                    //cancelCount();
                    alertdialog.dismiss();
                }
            }
        });
        txtResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
            }
        });
    }

    private void startCount(final TextView timer,final AlertDialog alertDialog)
    {

        countDownTimer:new CountDownTimer(120*1000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(""+String.format("0%d:%d ",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();
    }

    private void ifPhoneNumberExist(final String phone)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = databaseReference.orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    sendVerificationCode(phone);
                    setVerificationWindow();
                }else Toast.makeText(ForgetPassword.this,"Phone number doesn't exist.\nPlease create account first",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
