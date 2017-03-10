package com.tricktekno.optnio.oblu;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class session_list extends Activity {

    public static class SimpleListFragment extends ListFragment {
        File f;
        File[] file;
        List<String> filename;
        String path = ("/" + MainActivity.parentDir + "/" + MainActivity.saveDir + "/");

        public SimpleListFragment() {
            createDirIfNotExists(this.path);
            this.f = new File(Environment.getExternalStorageDirectory().toString() + this.path);
            this.file = this.f.listFiles();
            this.filename = new ArrayList();
        }

        public static boolean createDirIfNotExists(String path) {
            File file = new File(Environment.getExternalStorageDirectory(), path);
            if (file.exists()) {
                return true;
            }
            if (file.mkdirs()) {
                Log.i("FILE HANDLING", path + " Created successfully");
                return true;
            }
            Log.e("FILE ERROR ", "Problem creating folder");
            return false;
        }

        public void onListItemClick(ListView l, View v, int position, long id) {
            Toast.makeText(getActivity(), "Opening File  " + this.file[(int) id].getName(), 0).show();
            EventBus.getDefault().post(String.valueOf(this.file[(int) id].getName()));
            getActivity().finish();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            for (File name : this.file) {
                this.filename.add(name.getName());
            }
            if (this.file.length == 0) {
                Toast.makeText(getActivity(), "No saved sessions found", 0).show();
            }
            setListAdapter(new ArrayAdapter(inflater.getContext(), 17367043, this.filename));
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentById(16908290) == null) {
            fm.beginTransaction().add(16908290, new SimpleListFragment()).commit();
        }
    }
}