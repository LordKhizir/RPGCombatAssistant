package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.altekis.rpg.combatassistant.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ImportFileFragment extends SherlockListFragment implements View.OnClickListener {

    public static final String ARG_PATH = "path";

    public static ImportFileFragment newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        ImportFileFragment frg = new ImportFileFragment();
        frg.setArguments(args);
        return frg;
    }

    public static interface CallBack {
        void selectedFile(File file);
        void cancel();
    }

    Button mNavigationUpButton;
    Button mCancelButton;
    TextView mEmptyView;
    TextView mPathView;
    FolderAdapter mAdapter;

    CallBack mCallBack;
    File mFolder;
    FileComparator mComparator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CallBack) {
            mCallBack = (CallBack) activity;
        } else {
            throw new IllegalStateException("Activity must implement fragment's callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_import, container, false);
        mPathView = (TextView) view.findViewById(android.R.id.text1);
        mNavigationUpButton = (Button) view.findViewById(android.R.id.button1);
        mNavigationUpButton.setOnClickListener(this);
        mCancelButton = (Button) view.findViewById(android.R.id.button2);
        mCancelButton.setOnClickListener(this);
        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mComparator = new FileComparator();

        getListView().setEmptyView(mEmptyView);

        prepareFolder(savedInstanceState);

        if (isValidFile(mFolder, false)) {
            buildView();
        } else {
            mCallBack.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFolder != null) {
            outState.putString(ARG_PATH, mFolder.getAbsolutePath());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        File file = (File) mAdapter.getItem(position);
        if (file != null) {
            if (file.isDirectory()) {
                if (file.canRead()) {
                    mFolder = file;
                    buildView();
                } else {
                    Crouton.makeText(getSherlockActivity(), R.string.folder_inaccessible, Style.ALERT).show();
                }
            } else if (file.isFile()) {
                if (file.canRead()) {
                    mCallBack.selectedFile(file);
                } else {
                    Crouton.makeText(getSherlockActivity(), R.string.file_inaccesible, Style.ALERT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mNavigationUpButton) {
            if (mFolder != null && isValidFile(mFolder.getParentFile(), false)) {
                mFolder = mFolder.getParentFile();
                buildView();
            } else {
                Crouton.makeText(getSherlockActivity(), R.string.parent_folder_inaccessible, Style.ALERT).show();
            }
        } else if (v == mCancelButton) {
            mCallBack.cancel();
        }
    }

    void prepareFolder(Bundle savedInstanceState) {
        String path = null;
        if (savedInstanceState != null) {
            path = savedInstanceState.getString(ARG_PATH);
        } else if (getArguments() != null)  {
            path = getArguments().getString(ARG_PATH);
        }

        if (path != null) {
            mFolder = new File(path);
        }

        if (!isValidFile(mFolder, false)) {
            mCallBack.cancel();
        }
    }

    void buildView() {
        mPathView.setText(mFolder.getAbsolutePath());
        File[] contents = mFolder.listFiles(new ImportFileFilter());
        List<File> fileList = new ArrayList<File>();
        if (contents != null) {
            Collections.addAll(fileList, contents);
            Collections.sort(fileList, mComparator);
        }
        if (mAdapter == null) {
            mAdapter = new FolderAdapter(getSherlockActivity(), fileList);
            setListAdapter(mAdapter);
        } else {
            mAdapter.setFileList(fileList);
        }
    }

    boolean isValidFile(File folder, boolean file) {
        boolean valid = false;
        if (folder != null && folder.canRead()) {
            if (file && folder.isFile()) {
                valid = true;
            } else if (!file && folder.isDirectory()) {
                valid = true;
            }
        }
        return valid;
    }

    static class ImportFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || (file.isFile() && file.getName().endsWith(".zip"));
        }
    }

    static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File f1, File f2) {
            int compare;
            if ((f1.isDirectory() && f2.isDirectory()) || f1.isFile() && f2.isFile()) {
                compare = compare(f1.getName(), f2.getName());
            } else if (f1.isDirectory()) {
                compare = -1;
            } else {
                compare = 1;
            }
            return compare;
        }

        int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }
    }

    static class FolderAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<File> mFileList;

        FolderAdapter(Context context, List<File> fileList) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mFileList = fileList;
        }

        void setFileList(List<File> fileList) {
            mFileList = fileList;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return mFileList == null ? 0 : mFileList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            TextView textView;
            if (view == null) {
                view = mInflater.inflate(R.layout.fragment_file_import_item, parent, false);
                textView = (TextView) view.findViewById(android.R.id.text1);
                view.setTag(textView);
            } else {
                textView = (TextView) view.getTag();
            }

            File file = (File) getItem(position);
            textView.setText(file.getName());
            if (file.isDirectory()) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_archive, 0, 0, 0);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_set_as, 0, 0, 0);
            }

            return view;
        }
    }
}