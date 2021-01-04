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
public class Assoc_fragment extends Fragment {
    private String dept,file_uploaded_to,fnames,furls;
    private ListView lst_file;
    private List<Integer> imgids;
    private List<String> uploadobjurl;
    ArrayList<String> file_names;

    public Assoc_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        uploadobjurl =new ArrayList<>();
        imgids =new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String cid = mAuth.getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("association").child(cid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> someshot = new ArrayList<>();
                for (DataSnapshot dshot:dataSnapshot.getChildren()) {
                    someshot.add(dshot.getValue(String.class));

                }
                viewAssocUploads(someshot);
                /*Iterable<DataSnapshot> dshot =dataSnapshot.getChildren();
                ArrayList<String> someshot = new ArrayList<>();
                for (DataSnapshot d:dshot){
                    someshot.add(d.getValue(String.class));
                }*/
                //Toast.makeText(getActivity(),dshot.toString(),Toast.LENGTH_LONG).show();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_assoc_fragment, null);
        lst_file = root.findViewById(R.id.lst_fileassoc);

        lst_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectItem= file_names.get(i);
                String selectfile = file_names.get(i)+"_upload";
                String fileurls= uploadobjurl.get(i);
                //Toast.makeText(getContext(),selectItem+" "+fileurls,Toast.LENGTH_LONG).show();
                downloadAssocFiles(getActivity(),selectfile,DIRECTORY_DOWNLOADS,fileurls);
            }
        });
        // Inflate the layout for this fragment
        return root;
    }

    private  void viewAssocUploads( final ArrayList<String> assoc){
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("staff_uploads");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> files = (Map<String, Object>) dataSnapshot.getValue();
                file_names = new ArrayList<>();
                //iterate through each user, ignoring their UID
                for (Map.Entry<String, Object> entry : files.entrySet()) {
                    //Get user map
                    Map singleFile = (Map) entry.getValue();
                    //Get phone field and append to list
                    file_uploaded_to = (String) singleFile.get("uploadTo");

                        for (int j=0;j<assoc.size();j++) {
                            String mydep = "__" + assoc.get(j);
                            if (file_uploaded_to.equals(mydep)) {
                                //if (assoc.contains(file_uploaded_to)){
                                fnames = (String) singleFile.get("name");
                                furls = (String) singleFile.get("url");
                                file_names.add(fnames);
                                uploadobjurl.add(furls);
                               // Toast.makeText(getContext(), file_uploaded_to + " " + fnames + " " + furls, Toast.LENGTH_LONG).show();
                            }
                        }


                }
                if (file_names.size()==0){
                    Toast.makeText(getContext(),"No Files.",Toast.LENGTH_LONG).show();
                }
                else {
                    String[] finames = new String[file_names.size()];
                    Integer[] fimgids = new Integer[file_names.size()];
                    for (int i = 0; i < file_names.size(); i++) {
                        imgids.add(R.drawable.fileicon);
                    }
                    for (int j = 0; j < file_names.size(); j++) {
                        // Assign each value to String array
                        finames[j] = file_names.get(j);
                        fimgids[j] = R.drawable.fileicon;
                    }
                    Customlistadapter adapter = new Customlistadapter(getActivity(), finames, fimgids);
                    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Upload_files.this,android.R.layout.simple_list_item_1,uploadobj);
                    lst_file.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadAssocFiles(Context context, String filename, String dest_dir, String url){
        Toast.makeText(getContext(), "Downloading file "+filename+" to device.", Toast.LENGTH_LONG).show();
        DownloadManager downloadManager= (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri myurl=Uri.parse(url);
        DownloadManager.Request req =new DownloadManager.Request(myurl);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalFilesDir(context, dest_dir,filename);
        downloadManager.enqueue(req);
    }

}
