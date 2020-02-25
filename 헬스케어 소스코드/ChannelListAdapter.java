package kimsunho.medicalsignalendcoderapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/*   WaveViewer에서 채널 별 파형 속성 을 보기 위한 어댑터   */
public class ChannelListAdapter extends BaseAdapter {
    public Context mContext=null;       // 해당 Context 값
    public ArrayList<ListData> mListData=new ArrayList<ListData>();         // 채널리스트 저장하기 위한 배열리스트

    public ChannelListAdapter(Context mContext){            // 생성자
        super();                            // 상위클래스 생성자 호출
        this.mContext=mContext;             // 호출한 액티비티의 Context 변수 저장
    }

    public int getCount(){
        return mListData.size();
    }               // 채널의 사이즈 반환

    public Object getItem(int position){
        return mListData.get(position);
    }       // 채널의 값 반환

    public long getItemId(int position){
        return position;
    }           // 해당 위치 반환

    public void addItem(String channel,String wavetype){
        ListData addInfo=new ListData();                    // ListData -> 채널리스트, 파형속성을 저장하기위한 클래스
        addInfo.channelString=channel;                  // 채널 저장
        addInfo.wavetypeString=wavetype;                // 채널별 파형 속성 저장

        mListData.add(addInfo);                         // 저장
    }

    public void remove(int position){               // 해당 채널, 파형속성 제거
        mListData.remove(position);     // 해당 데이터 제거
        dataChange();                   // 데이터의 변화가 있을 시 호출하는 메서드
    }

    public void clear() {
        mListData.clear();
    }       // 채널리스트 초기화 -> 완전히 지움

    public void dataChange(){
        notifyDataSetChanged();             // 채널리스트 변화가 있을 시 호출 하는 메서드 -> 기존에 정의 되어있으,ㅁ
    }
    /*   해당 아이템들을 보여주는 뷰 메서드   */
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder;                   // 해당 뷰를 그려주기위해 정의한 뷰홀더

        if(convertView==null) {                 // converView가 아무런 값을 가지지 않을 때
            holder = new ViewHolder();              // 해당 뷰를 그려주기위해 정의한 뷰홀더 생성자 호출

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);              // 레이아웃 서비스 사용 -> 자바에서
            convertView = inflater.inflate(R.layout.customlistview2, null);             // 해당 xml의 레이아웃을 불러옴 -> 뷰에 저장

            holder.textView1=(TextView)convertView.findViewById(R.id.channeltextview);          // xml에서 정의된 뷰아이디 저장
            holder.textView2=(TextView)convertView.findViewById(R.id.wavetypetextview);         // xml에서 정의된 뷰아이디 저장

            convertView.setTag(holder);         // 해당 뷰를 Set -> 뷰 저장? -> 사용하기 위해서 -> 뷰에 보여주기 위해서
        }
        else{
            holder=(ViewHolder)convertView.getTag();                // convertView가 값을 가질 때
        }

        ListData mData=mListData.get(position);                 // 값을 얻어옴

        holder.textView1.setText(mData.channelString);          // 값을 뷰에 보여줌
        holder.textView2.setText(mData.wavetypeString);        // 값을 뷰에 보여줌

        return convertView;         // 뷰 반환
    }
    /*   해당 뷰 위젯을 보여주기 위한 정의한 클래스   */
    public class ViewHolder {
        public TextView textView1;              // TextView 변수 1
        public TextView textView2;              // TextView 변수 2
    }

    public class ListData {
        public String channelString;            // 채널 넘버 저장
        public String wavetypeString;           // 파형 속성 저장
    }

}
