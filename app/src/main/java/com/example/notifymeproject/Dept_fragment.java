package com.example.notifymeproject;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


/**
 * A simple {@link Fragment} subclass.
 */
public class Dept_fragment extends Fragment {
    private String dept,file_uploadedto,fname,furl;
    private ListView lst_files;
    private List<Integer> imgid;
    private List<String> uploadobjurls;
    ArrayList<String> file_name;

    public Dept_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        uploadobjurls =new ArrayList<>();
        imgid =new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String cid = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(cid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dept = dataSnapshot.child("udep").getValue(String.class);
                //Toast.makeText(getContext(),dept,Toast.LENGTH_LONG).show();
                viewDepUploads(dept);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_dept_fragment, null);
        lst_files = root.findViewById(R.id.lst_filedep);

        lst_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem= file_name.get(i);
                String selectedfile = file_name.get(i)+"_upload";
                String fileurl= uploadobjurls.get(i);
                //Toast.makeText(getContext(),selectedItem+" "+fileurl,Toast.LENGTH_LONG).show();
                downloadFiles(getActivity(),selectedfile,DIRECTORY_DOWNLOADS,fileurl);
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    private  void viewDepUploads(String dep){
        final String mydep= dep+"__";
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("staff_uploads");
        //myref.orderByChild("uploadTo").equalTo(searchuploads).addListenerForSingleValueEvent(new ValueEventListener() {
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //fname = dataSnapshot.child("name").getValue(String.class);
                    alluploads(mydep,(Map<String,Object>) dataSnapshot.getValue());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void alluploads(String uploadedto,Map<String, Object> files) {
        file_name = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : files.entrySet()){

            //Get user map
            Map singleFile = (Map) entry.getValue();
            //Get phone field and append to list

            file_uploadedto=(String)singleFile.get("uploadTo");
            //assert file_uploadedto != null;
            if(file_uploadedto.equals(uploadedto)){
                fname=(String)singleFile.get("name");
                furl = (String)singleFile.get("url");
                file_name.add(fname);
                uploadobjurls.add(furl);
                //Toast.makeText(getContext(),file_uploadedto+" "+fname+" "+furl,Toast.LENGTH_LONG).show();
            }
           /* else{
                Toast.makeText(getContext(),"No Files.",Toast.LENGTH_LONG).show();
            }*/
            //Toast.makeText(this,file_uploadedto+fname+uploadedto,Toast.LENGTH_LONG).show();
        }
        if (file_name.size()==0){
            Toast.makeText(getContext(),"No Files.",Toast.LENGTH_LONG).show();
        }
        else {
            String[] finame = new String[file_name.size()];
            Integer[] fimgid = new Integer[file_name.size()];
            for (int i = 0; i < file_name.size(); i++) {
                imgid.add(R.drawable.fileicon);
            }
            for (int j = 0; j < file_name.size(); j++) {
                // Assign each value to String array
                finame[j] = file_name.get(j);
                fimgid[j] = R.drawable.fileicon;
            }
            Customlistadapter adapter = new Customlistadapter(getActivity(), finame, fimgid);
            //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Upload_files.this,android.R.layout.simple_list_item_1,uploadobj);
            lst_files.setAdapter(adapter);
        }
    }

    private void downloadFiles(Context context, String filename, String dest_dir, String url){
        Toast.makeText(getContext(), "Downloading file "+filename+" to device.", Toast.LENGTH_LONG).show();
        DownloadManager downloadManager= (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri myurl=Uri.parse(url);
        DownloadManager.Request req =new DownloadManager.Request(myurl);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalFilesDir(context, dest_dir,filename);
        downloadManager.enqueue(req);
    }
}
