package com.example.nomanikram.epilepsyseizuredetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nomanikram.epilepsyseizuredetection.models.Contact;
import com.example.nomanikram.epilepsyseizuredetection.models.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //  declarign the bottom view variable
    private BottomNavigationView bottom;

    // declaring the variable for firebase auth state and database ref
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    // declare variable for counter purpise
    private static int no;
    private static String total_no;

    // declaring variable for contact
    private static Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialing the bottom nav
        bottom = (BottomNavigationView) findViewById(R.id.bottom);

        // initializing with firebase auth state and database red
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        Log.w("", "UID: " + mAuth.getCurrentUser().getUid());

        HomeFragment fragment = new HomeFragment();
        setFragment(fragment);

//
//        mAuth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance().getReference();

        // saving new contacts to firebase database
        update_contacts();

        // query to get changes at node
        Query query1 = database.child("users").equalTo(mAuth.getCurrentUser().getUid());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                    Log.w("CHECKING: ", "" + singleSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Botton nav and fragment setters
        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {

                    HomeFragment fragment = new HomeFragment();
                    setFragment(fragment);


                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_LONG);
                    Log.w("TAG", "Home");
                } else if (id == R.id.nav_patient) {

                    PatientFragment fragment = new PatientFragment();
                    setFragment(fragment);

                    Log.w("TAG", "Patient");

                    Toast.makeText(MainActivity.this, "Patient", Toast.LENGTH_LONG);
                } else if (id == R.id.nav_contact) {
                    ContactFragment fragment = new ContactFragment();
                    setFragment(fragment);
                    Log.w("TAG", "Contact");

                } else if (id == R.id.nav_records) {
                    RecordFragment fragment = new RecordFragment();
                    setFragment(fragment);

                    Log.w("TAG", "Record");
                } else if (id == R.id.nav_setting) {

                    SettingFragment fragment = new SettingFragment();
                    setFragment(fragment);

                    Log.w("TAG", "Setting");
                }
                return true;
            }
        });
    }

    // Setting Fragment on View
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.context, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    public static class SensorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Data data = (Data) intent.getSerializableExtra("MyObject");

            String txt_pulse = data.pulse;
            String txt_temp = data.temp;

            Log.w("TAG", "MainActivity\n" + "temp: " + txt_temp + "\npulse: " + txt_pulse);
        }
    }

    private void update_contacts() {

       contact = (Contact) getIntent().getSerializableExtra("MyObject");

        if (contact != null) {

            DatabaseReference user_reference = database.child("users").child("" + FirebaseAuth.getInstance().getCurrentUser().getUid());

            final DatabaseReference contact_ref = database.child("users").child("" + mAuth.getCurrentUser().getUid()).child("Patient").child("Contact");


            Query query = contact_ref;

                query.addListenerForSingleValueEvent((new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (!dataSnapshot.child("Size").exists())
                        {
                            no = 0;
                            total_no="0";

                            contact_ref.child("Size").setValue("" + total_no);
                            contact_ref.child("contact " + total_no).child("Name").setValue("" + contact.contact_name);
                            contact_ref.child("contact " + total_no).child("Number").setValue("" + contact.contact_no);
                        }
                        else
                        {
                            Log.w("","total_no: "+total_no);
                            total_no = (String) dataSnapshot.child("Size").getValue();
                            no = Integer.parseInt(total_no);

                            no++;
                            total_no = ""+no;

                            contact_ref.child("Size").setValue("" + total_no);
                            contact_ref.child("contact " + total_no).child("Name").setValue("" + contact.contact_name);
                            contact_ref.child("contact " + total_no).child("Number").setValue("" + contact.contact_no);

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }));



        }

    }
}