package kimsunho.medicalsignalendcoderapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*   InfoList 어댑터    */
public class InfoListAdapter extends BaseAdapter{
    public Context mContext=null;

    /*   따로 둔 이유는 ListData 클래스에서 데이터를 따로 분리가 안되므로 따로 저장하는 리스트 배열을 선언   */
    public ArrayList<ListData> mListData=new ArrayList<ListData>();                    // tag,value 값 저장
    public ArrayList<String> tagstring=new ArrayList<String>();                     // tag string 값 저장
    public ArrayList<String> valuestring=new ArrayList<String>();                   // value string 값 저장

    public InfoListAdapter(Context mContext){
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

    public void addItem(String tagstr,String valuestr){
        ListData addInfo=new ListData();
        addInfo.mTagTitle=tagstr;
        addInfo.mValTitle=valuestr;

        mListData.add(addInfo);
        tagstring.add(tagstr);
        valuestring.add(valuestr);
    }

    public void addIndexItem(int index,String tagstr,String valuestr){
        ListData addInfo=new ListData();
        addInfo.mTagTitle=tagstr;
        addInfo.mValTitle=valuestr;

        mListData.add(index,addInfo);
        tagstring.add(index,tagstr);
        valuestring.add(index,valuestr);
    }

    public void remove(int position){
        tagstring.remove(position);
        valuestring.remove(position);
        mListData.remove(position);
        dataChange();
    }

    public void clear() {
        tagstring.clear();
        valuestring.clear();
        mListData.clear();
    }

    public void dataChange(){
        notifyDataSetChanged();
    }

    public View getView(int position,View convertView,ViewGroup parent){

        ViewHolder holder;

        if(convertView==null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.customlistview3, null);

            holder.tagText=(TextView) convertView.findViewById(R.id.tagtextview);
            holder.valueText=(TextView) convertView.findViewById(R.id.valuetextview);

            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }

        ListData mData=mListData.get(position);

        holder.tagText.setText(mData.mTagTitle);
        holder.valueText.setText(mData.mValTitle);

        return convertView;
    }

    public class ViewHolder {
        public TextView tagText;
        public TextView valueText;
    }

    public class ListData {

        public String mTagTitle;
        public String mValTitle;
    }
}
