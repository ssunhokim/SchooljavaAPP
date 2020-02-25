package kimsunho.medicalsignalendcoderapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FileListAdapter extends BaseAdapter {
    public Context mContext=null;
    public ArrayList<ListData> mListData=new ArrayList<ListData>();

    public FileListAdapter(Context mContext){
        super();
        this.mContext=mContext;
    }

    public int getCount(){
        return mListData.size();
    }

    public Object getItem(int position){
        return mListData.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public void addItem(String str){
        ListData addInfo=new ListData();

        addInfo.string=str;

        mListData.add(addInfo);
    }

    public void remove(int position){

    }

    public void clear() {
    }

    public void dataChange(){
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder;

        if(convertView==null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.customlistview, null);

            holder.imageView=(ImageView)convertView.findViewById(R.id.adapterimageView1);
            holder.textView=(TextView)convertView.findViewById(R.id.adaptertextView1);

            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }

        ListData mData=mListData.get(position);

        holder.textView.setText(mData.string);

        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    public class ListData {
        Drawable image;
        String string;
    }
}
