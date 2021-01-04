package com.example.notifymeproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Upload_files extends AppCompatActivity {
    String cid,type,dept,sem,cour,stud_cour,stud_sem;
    private FirebaseAuth mAuth;
    LinearLayout uploadparams;
    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    //these are the views
    EditText filename;
    private ProgressBar progressBar;
    Spinner cat_course, sems ;
    ArrayAdapter adp1,adp2;
    CheckBox uploaddep;

    //List<String> uploadobjurls;
    //the firebase objects for storage and database
    DatabaseReference ref,dbref;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);

        Toolbar myToolbar =  findViewById(R.id.tb_upload);
        myToolbar.setTitle("Upload Files");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        cid = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference();
        filename = findViewById(R.id.txt_filename);
        cat_course = findViewById(R.id.spnr_category);
        adp1= ArrayAdapter.createFromResource(this,R.array.Reg_cat_course,R.layout.support_simple_spinner_dropdown_item);
        cat_course.setAdapter(adp1);
        sems = findViewById(R.id.spnr_term);

        cat_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(cat_course.getSelectedItem().toString().equals("Association")){
                    adp2= ArrayAdapter.createFromResource(Upload_files.this,R.array.Reg_association,R.layout.support_simple_spinner_dropdown_item);
                    //sems.setAdapter(adp2);
                }
                else{
                    adp2= ArrayAdapter.createFromResource(Upload_files.this,R.array.Reg_sems,R.layout.support_simple_spinner_dropdown_item);
                    //sems.setAdapter(adp2);
                }
                sems.setAdapter(adp2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
                if(cat_course.getSelectedItem().toString().equals("Association")){
                    adp2= ArrayAdapter.createFromResource(Upload_files.this,R.array.Reg_association,R.layout.support_simple_spinner_dropdown_item);
                    //sems.setAdapter(adp2);
                }
                else{
                    adp2= ArrayAdapter.createFromResource(Upload_files.this,R.array.Reg_sems,R.layout.support_simple_spinner_dropdown_item);
                    //sems.setAdapter(adp2);
                }
                sems.setAdapter(adp2);

        uploaddep= findViewById(R.id.cbox_uploadtodept);
        uploaddep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(uploaddep.isChecked()){
                    cat_course.setEnabled(false);
                    sems.setEnabled(false);
                }
                else{
                    cat_course.setEnabled(true);
                    sems.setEnabled(true);
                }
            }
        });


       /* adp2= ArrayAdapter.createFromResource(this,R.array.Reg_sems,R.layout.support_simple_spinner_dropdown_item);
        sems.setAdapter(adp2);*/
        progressBar = findViewById(R.id.pb_login);
        uploadparams = findViewById(R.id.upload_param);



    }

    @Override
    protected void onStart() {
        super.onStart();
        //cid = mAuth.getCurrentUser().getUid();
        //Toast.makeText(this, cid, Toast.LENGTH_SHORT).show();
        dbref=FirebaseDatabase.getInstance().getReference().child("users").child(cid);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                type = dataSnapshot.child("usertype").getValue(String.class);
                stud_sem = dataSnapshot.child("usem").getValue(String.class);
                dept = dataSnapshot.child("udep").getValue(String.class);
                stud_cour = dataSnapshot.child("ucourse").getValue(String.class);
                //}
                //Toast.makeText(Upload_files.this,dept+type,Toast.LENGTH_SHORT).show();
                if(type.equals("Staff")){
                    uploadparams.setVisibility(View.VISIBLE);
                }
                else
                    uploadparams.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Please provide permission to access device storage.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("*/*");
        //intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected uploading the file
                upload(data.getData());
        }
        else{
            Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
        }
    }

    protected void upload(Uri filedata){
        /*final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();*/
        String filenm = filename.getText().toString().replace(" ","_").trim();
        progressBar.setVisibility(View.VISIBLE);

        if(type.equals("Staff")){
            if(uploaddep.isChecked()){
                uploadToStorage("staff_uploads",cid,filenm,dept,"","",filedata);
        }
            else{
                cour=cat_course.getSelectedItem().toString();
                //cour=cour.replace(".","");
                sem=sems.getSelectedItem().toString();
                if(cour.equals("Association")){
                     /*String assoc= sems.getSelectedItem().toString();
                     String new_assoc_value=assoc.replace(" ","_");*/
                    uploadToStorage("staff_uploads",cid,filenm,"","",sem,filedata);
                }
                else{
                    uploadToStorage("staff_uploads",cid,filenm,dept,cour,sem,filedata);
                }
            }
        }
        else {
            cour= stud_cour;
            sem = stud_sem;
            uploadToStorage("student_uploads",cid,filenm,dept,cour,sem,filedata);
        }



        //StorageReference sRef = mStorageRef.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        //StorageReference sRef = mStorageRef.child(Constants.STORAGE_PATH_UPLOADS);


    }

    public void uploadToStorage(final String uploadType,final String id, final String file_name,final String department,final String course,final String semester, Uri data){
        StorageReference sRef = mStorageRef.child(id).child(file_name);
        sRef.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete());
                Uri url= uri.getResult();

                String upload_to=department+"_"+course+"_"+semester;
                Uploads fileupload = new Uploads(file_name,url.toString(),upload_to);

                String fid = file_name+"_upload";
                ref.child(uploadType).child(fid).setValue(fileupload);
                Toast.makeText(Upload_files.this,"File Uploaded Successfully",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                //uploadobj.add(filename.getText().toString().trim());
            }
        })






                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=  (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressBar.setProgress((int)progress);

                        // progressDialog.setMessage("Uploaded: "+(int)progress+"%");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Upload_files.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }




    public void upload_files(View view) {
        if (filename.length()==0){
            Toast.makeText(this,"Please enter filename", Toast.LENGTH_SHORT).show();
        }
        else
            getPDF();
    }


    public void view_upload_files(View view) {
        startActivity(new Intent(Upload_files.this,view_files_by_me.class));
    }
}
